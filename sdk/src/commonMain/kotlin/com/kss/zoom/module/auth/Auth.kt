package com.kss.zoom.module.auth

import com.kss.zoom.client.ApiClient
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.map
import com.kss.zoom.module.auth.Auth.Companion.FORM_URL_ENCODED_CONTENT_TYPE
import com.kss.zoom.module.auth.Auth.Companion.baseUrl
import com.kss.zoom.module.auth.model.AuthConfig
import com.kss.zoom.module.auth.model.AuthResponse
import com.kss.zoom.module.auth.model.UserTokens
import com.kss.zoom.module.auth.model.toUserTokens
import io.ktor.http.*

interface Auth {
    companion object {
        const val FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded"

        val baseUrl = Url("https://zoom.us")

        private val authUrlBuilder = URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = baseUrl.host
            encodedPath = "/oauth/authorize"
            parameters.append("response_type", "code")
        }

        fun create(config: AuthConfig, client: ApiClient): Auth {
            return DefaultAuth(config, client)
        }

        fun getAuthorizationUrl(clientId: String, callbackUrl: String): String {
            return authUrlBuilder.apply {
                parameters.append("client_id", clientId)
                parameters.append("redirect_uri", callbackUrl)
            }.build().toString()
        }
    }

    suspend fun authorize(code: String): CallResult<Unit>
    suspend fun accessToken(): CallResult<String>
    suspend fun refreshTokens(): CallResult<Unit>
}

private class DefaultAuth(private val config: AuthConfig, private val client: ApiClient) : Auth {

    private val oauthTokenUrl = "$baseUrl/oauth/token"

    private lateinit var userTokens: UserTokens

    override suspend fun authorize(code: String): CallResult<Unit> {
        return client.post<AuthResponse>(
            url = oauthTokenUrl,
            clientId = config.clientId,
            clientSecret = config.clientSecret,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = null
        ).map { response ->
            // Save the access and refresh tokens
            this.userTokens = response.toUserTokens()
        }
    }

    override suspend fun accessToken(): CallResult<String> {
        return if (userTokens.isExpired()) {
            when (refreshTokens()) {
                is CallResult.Success -> CallResult.Success(userTokens.accessToken)
                else -> CallResult.Error("Failed to refresh access token")
            }
        } else {
            CallResult.Success(userTokens.accessToken)
        }
    }

    override suspend fun refreshTokens(): CallResult<Unit> {
        return client.post<AuthResponse>(
            url = oauthTokenUrl,
            body = "grant_type=refresh_token&refresh_token=${userTokens.refreshToken}",
            contentType = FORM_URL_ENCODED_CONTENT_TYPE
        ).map { response ->
            // Save the new access and refresh tokens
            this.userTokens = response.toUserTokens()
        }
    }
}
