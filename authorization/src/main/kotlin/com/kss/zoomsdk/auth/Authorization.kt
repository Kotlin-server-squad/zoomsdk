package com.kss.zoomsdk.auth

import com.kss.zoomsdk.auth.config.AuthorizationConfig
import com.kss.zoomsdk.client.WebClient
import com.kss.zoomsdk.client.WebClient.Companion.FORM_URL_ENCODED_CONTENT_TYPE
import io.ktor.client.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant

interface IAuthorization {
    /**
     * Authorize a user with the given code and exchange it for a pair of access and refresh tokens.
     * @param code The code received from Zoom OAuth callback.
     * @return User authorization as a pair of access and refresh tokens.
     * @throws AuthorizationException
     */
    suspend fun authorizeUser(code: String): UserAuthorization

    /**
     * Refresh the user authorization with the given refresh token.
     * @param refreshToken The refresh token received from Zoom OAuth callback.
     * @return Renewed user authorization as a pair of access and refresh tokens.
     * @throws AuthorizationException
     */
    suspend fun refreshUserAuthorization(refreshToken: String): UserAuthorization

    /**
     * Get the authorization URL to redirect the user to.
     * @param callbackUrl The URL to redirect the user to after authorization.
     * @return The authorization URL.
     */
    fun getAuthorizationUrl(callbackUrl: String): URL
}

class Authorization(private val config: AuthorizationConfig, private val client: WebClient) : IAuthorization {

    companion object {
        fun create(config: AuthorizationConfig): IAuthorization {
            return Authorization(config, WebClient.create())
        }

        fun create(config: AuthorizationConfig, httpClient: HttpClient): IAuthorization {
            return Authorization(config, WebClient.create(httpClient))
        }
    }

    private val oauthTokenUrl = "${config.baseUrl}/oauth/token"

    override suspend fun authorizeUser(code: String): UserAuthorization =
        client.post<Zoom.AccessTokenResponse>(
            url = oauthTokenUrl,
            token = code,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = "grant_type=authorization_code&code=$code"
        ).map { it.toUserAuthorization() }.getOrElse { throw AuthorizationException(it) }

    override suspend fun refreshUserAuthorization(refreshToken: String): UserAuthorization =
        client.post<Zoom.AccessTokenResponse>(
            url = oauthTokenUrl,
            body = "grant_type=refresh_token&refresh_token=$refreshToken",
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
        ).map { it.toUserAuthorization() }.getOrElse { throw AuthorizationException(it) }

    override fun getAuthorizationUrl(callbackUrl: String): URL =
        URL(
            "${config.baseUrl}/oauth/authorize?response_type=code&client_id=${config.clientId}&redirect_uri=${
                encode(
                    callbackUrl
                )
            }"
        )

    private fun encode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)
}

private object Zoom {
    @Serializable
    data class AccessTokenResponse(
        @SerialName("access_token") val accessToken: String,
        @SerialName("token_type") val tokenType: String,
        @SerialName("refresh_token") val refreshToken: String,
        @SerialName("scope") val scope: String
    ) {
        fun toUserAuthorization(): UserAuthorization {
            return UserAuthorization(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresAt = Instant.now().epochSecond + 60 * 60
            )

        }
    }
}

data class UserAuthorization(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long
)