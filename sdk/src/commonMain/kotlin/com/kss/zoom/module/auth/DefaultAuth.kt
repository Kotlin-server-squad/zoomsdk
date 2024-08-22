package com.kss.zoom.module.auth

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.extensions.map
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.Auth.Companion.FORM_URL_ENCODED_CONTENT_TYPE
import com.kss.zoom.module.auth.model.*
import io.ktor.http.*

class DefaultAuth(private val config: AuthConfig, private val client: ApiClient) : Auth {

    private val oauthTokenUrl = "${config.baseUrl}/oauth/token"

    override val accountId: String
        get() = config.accountId

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
        return client.post<UserAuthResponse>(
            url = oauthTokenUrl,
            clientId = config.clientId,
            clientSecret = config.clientSecret,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = null
        ).map { it.toUserTokens() }
    }

    override suspend fun reauthorize(refreshToken: String): CallResult<UserTokens> {
        return client.post<UserAuthResponse>(
            url = oauthTokenUrl,
            body = "grant_type=refresh_token&refresh_token=${refreshToken}",
            contentType = FORM_URL_ENCODED_CONTENT_TYPE
        ).map { it.toUserTokens() }
    }

    override suspend fun authorizeAccount(): CallResult<AccountToken> {
        return client.post<AccountAuthResponse>(
            url = oauthTokenUrl,
            clientId = config.clientId,
            clientSecret = config.clientSecret,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = "grant_type=account_credentials&account_id=${config.accountId}"
        ).map { it.toAccountToken() }
    }
}
