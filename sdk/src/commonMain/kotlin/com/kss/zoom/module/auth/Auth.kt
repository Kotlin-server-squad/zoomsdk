package com.kss.zoom.module.auth

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.extensions.map
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.Auth.Companion.FORM_URL_ENCODED_CONTENT_TYPE
import com.kss.zoom.module.auth.model.AuthConfig
import com.kss.zoom.module.auth.model.AuthResponse
import com.kss.zoom.module.auth.model.UserTokens
import com.kss.zoom.module.auth.model.toUserTokens
import io.ktor.http.*

interface Auth {
    companion object {
        const val FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded"

        fun create(config: AuthConfig, client: ApiClient): Auth {
            return DefaultAuth(config, client)
        }
    }

    fun getAuthorizationUrl(callbackUrl: String): String
    suspend fun authorize(code: String): CallResult<UserTokens>
    suspend fun reauthorize(refreshToken: String): CallResult<UserTokens>
}

private class DefaultAuth(private val config: AuthConfig, private val client: ApiClient) : Auth {

    private val oauthTokenUrl = "${config.baseUrl}/oauth/token"

    override fun getAuthorizationUrl(callbackUrl: String): String {
        return URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = config.baseUrl.host
            encodedPath = "/oauth/authorize"
            parameters.append("response_type", "code")
            parameters.append("client_id", config.clientId)
            parameters.append("redirect_uri", callbackUrl)
        }.buildString()
    }

    override suspend fun authorize(code: String): CallResult<UserTokens> {
        return client.post<AuthResponse>(
            url = oauthTokenUrl,
            clientId = config.clientId,
            clientSecret = config.clientSecret,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = null
        ).map { it.toUserTokens() }
    }

    override suspend fun reauthorize(refreshToken: String): CallResult<UserTokens> {
        return client.post<AuthResponse>(
            url = oauthTokenUrl,
            body = "grant_type=refresh_token&refresh_token=${refreshToken}",
            contentType = FORM_URL_ENCODED_CONTENT_TYPE
        ).map { it.toUserTokens() }
    }
}
