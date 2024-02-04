package com.kss.zoom.auth

import com.kss.zoom.auth.Http.toUserAuthorization
import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.client.WebClient
import com.kss.zoom.client.WebClient.Companion.FORM_URL_ENCODED_CONTENT_TYPE
import io.ktor.client.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

interface IAuthorization {
    /**
     * Authorize a user with the given code and exchange it for a pair of access and refresh tokens.
     * @param code The code received from Zoom OAuth callback.
     * @return User authorization as a pair of access and refresh tokens.
     * @throws AuthorizationException
     */
    suspend fun authorizeUser(code: AuthorizationCode): UserAuthorization

    /**
     * Refresh the user authorization with the given refresh token.
     * @param refreshToken The refresh token received from Zoom OAuth callback.
     * @return Renewed user authorization as a pair of access and refresh tokens.
     * @throws AuthorizationException
     */
    suspend fun refreshUserAuthorization(refreshToken: RefreshToken): UserAuthorization

    /**
     * Get the authorization URL to redirect the user to.
     * @param callbackUrl The URL to redirect the user to after authorization.
     * @return The authorization URL.
     */
    fun getAuthorizationUrl(callbackUrl: URL): URL
}

class Authorization(private val config: AuthorizationConfig, private val client: WebClient) : IAuthorization {

    companion object {
        fun create(config: AuthorizationConfig, httpClient: HttpClient? = null): Authorization {
            return Authorization(config, httpClient?.let { WebClient.create(it) } ?: WebClient.create())
        }
    }

    private val oauthTokenUrl = "${config.baseUrl}/oauth/token"

    override suspend fun authorizeUser(code: AuthorizationCode): UserAuthorization =
        client.post<Http.UserAuthorizationResponse>(
            url = oauthTokenUrl,
            token = code.value,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = "grant_type=authorization_code&code=$code"
        ).toUserAuthorization()

    override suspend fun refreshUserAuthorization(refreshToken: RefreshToken): UserAuthorization =
        client.post<Http.UserAuthorizationResponse>(
            url = oauthTokenUrl,
            body = "grant_type=refresh_token&refresh_token=${refreshToken.value}",
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
        ).toUserAuthorization()

    override fun getAuthorizationUrl(callbackUrl: URL): URL =
        URL(
            "${config.baseUrl}/oauth/authorize?response_type=code&client_id=${config.clientId.value}&redirect_uri=${
                encode(
                    callbackUrl.toString()
                )
            }"
        )

    private fun encode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)
}

private object Http {
    @Serializable
    data class UserAuthorizationResponse(
        @SerialName("access_token") val accessToken: String,
        @SerialName("token_type") val tokenType: String,
        @SerialName("refresh_token") val refreshToken: String,
        @SerialName("expires_in") val expiresIn: Long
    ) {
        fun toUserAuthorization(): UserAuthorization {
            return UserAuthorization(
                accessToken = AccessToken(accessToken, expiresIn),
                refreshToken = RefreshToken(refreshToken)
            )
        }
    }

    fun Result<UserAuthorizationResponse>.toUserAuthorization(): UserAuthorization =
        map { it.toUserAuthorization() }.getOrElse { throw AuthorizationException(it) }
}
