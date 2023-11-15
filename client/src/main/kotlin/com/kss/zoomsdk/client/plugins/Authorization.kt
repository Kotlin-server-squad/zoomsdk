package com.kss.zoomsdk.client.plugins

import com.kss.zoomsdk.client.ZoomClientConfig
import com.kss.zoomsdk.client.plugins.Http.Companion.FORM_URL_ENCODED_CONTENT_TYPE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant

interface IAuthorization {
    suspend fun authorizeUser(code: String): Result<UserAuthorization>
    suspend fun reauthorizeUser(refreshToken: String): Result<UserAuthorization>
    fun getAuthorizationUrl(callbackUrl: String): Result<URL>
}

class Authorization(private val config: ZoomClientConfig, private val http: Http) : IAuthorization {

    private val oauthTokenUrl = "${config.baseUrl}/oauth/token"

    override suspend fun authorizeUser(code: String): Result<UserAuthorization> =
        http.post<Zoom.AccessTokenResponse>(
            url = oauthTokenUrl,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = "grant_type=authorization_code&code=$code"
        ).map { it.toUserAuthorization() }

    override suspend fun reauthorizeUser(refreshToken: String): Result<UserAuthorization> =
        http.post<Zoom.AccessTokenResponse>(
            url = oauthTokenUrl,
            body = "grant_type=refresh_token&refresh_token=$refreshToken",
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
        ).map { it.toUserAuthorization() }

    override fun getAuthorizationUrl(callbackUrl: String): Result<URL> {
        return Result.success(
            URL(
                "${config.baseUrl}/oauth/authorize?response_type=code&client_id=${config.clientId}&redirect_uri=${
                    encode(
                        callbackUrl
                    )
                }"
            )
        )
    }

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