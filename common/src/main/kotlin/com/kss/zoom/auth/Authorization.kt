package com.kss.zoom.auth

import com.kss.zoom.auth.Http.UserAuthorizationResponse
import com.kss.zoom.auth.Http.toUserTokens
import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.client.WebClient
import com.kss.zoom.client.WebClient.Companion.FORM_URL_ENCODED_CONTENT_TYPE
import com.kss.zoom.toWebClient
import io.ktor.client.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

interface Authorization {
    /**
     * Authorize a user with the given code and exchange it for a pair of access and refresh tokens.
     * @param code The code received from Zoom OAuth callback.
     * @return User authorization as a pair of access and refresh tokens.
     */
    suspend fun authorizeUser(code: AuthorizationCode): Result<UserTokens>

    /**
     * Refresh the user authorization with the given refresh token.
     * @param refreshToken The refresh token received from Zoom OAuth callback.
     * @return Renewed user authorization as a pair of access and refresh tokens.
     */
    suspend fun refreshUserAuthorization(refreshToken: RefreshToken): Result<UserTokens>

    /**
     * Get the authorization URL to redirect the user to.
     * @param callbackUrl The URL to redirect the user to after authorization.
     * @return The authorization URL.
     */
    fun getAuthorizationUrl(callbackUrl: URL): URL
}

class AuthorizationImpl(private val config: AuthorizationConfig, private val client: WebClient) : Authorization {

    companion object {
        fun create(config: AuthorizationConfig, httpClient: HttpClient? = null): AuthorizationImpl {
            return AuthorizationImpl(config, httpClient.toWebClient())
        }
    }

    private val oauthTokenUrl = "${config.baseUrl}/oauth/token"

    override suspend fun authorizeUser(code: AuthorizationCode): Result<UserTokens> =
        client.post<UserAuthorizationResponse>(
            url = oauthTokenUrl,
            token = code.value,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = "grant_type=authorization_code&code=${code.value}"
        ).toUserTokens()

    override suspend fun refreshUserAuthorization(refreshToken: RefreshToken): Result<UserTokens> =
        client.post<UserAuthorizationResponse>(
            url = oauthTokenUrl,
            body = "grant_type=refresh_token&refresh_token=${refreshToken.value}",
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
        ).toUserTokens()

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
        fun toUserTokens(): UserTokens {
            return UserTokens(
                accessToken = AccessToken(accessToken, expiresIn),
                refreshToken = RefreshToken(refreshToken)
            )
        }
    }

    fun Result<UserAuthorizationResponse>.toUserTokens(): Result<UserTokens> =
        this.map { it.toUserTokens() }
}
