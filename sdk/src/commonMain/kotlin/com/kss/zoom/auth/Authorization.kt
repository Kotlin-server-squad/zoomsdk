package com.kss.zoom.auth

import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.auth.model.*
import com.kss.zoom.auth.model.api.ServerAuthorizationResponse
import com.kss.zoom.auth.model.api.UserAuthorizationResponse
import com.kss.zoom.auth.model.api.toAccessToken
import com.kss.zoom.auth.model.api.toUserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.client.WebClient.Companion.FORM_URL_ENCODED_CONTENT_TYPE
import com.kss.zoom.sdk.common.model.Url
import io.ktor.http.*

class Authorization(private val config: AuthorizationConfig, private val client: WebClient) : IAuthorization {

    private val oauthTokenUrl = "${config.baseUrl.value}/oauth/token"

    override suspend fun generateAccessToken(accountId: AccountId): Result<AccessToken> =
        client.post<ServerAuthorizationResponse>(
            url = oauthTokenUrl,
            clientId = config.clientId.value,
            clientSecret = config.clientSecret.value,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = "grant_type=account_credentials&account_id=${accountId.value}"
        ).toAccessToken()


    override suspend fun authorizeUser(code: AuthorizationCode): Result<UserTokens> =
        client.post<UserAuthorizationResponse>(
            url = "$oauthTokenUrl?grant_type=authorization_code&code=${code.value}&redirect_uri=http://localhost:8080/callback",
            clientId = config.clientId.value,
            clientSecret = config.clientSecret.value,
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
            body = null
        ).toUserTokens()

    override suspend fun refreshUserAuthorization(refreshToken: RefreshToken): Result<UserTokens> =
        client.post<UserAuthorizationResponse>(
            url = oauthTokenUrl,
            body = "grant_type=refresh_token&refresh_token=${refreshToken.value}",
            contentType = FORM_URL_ENCODED_CONTENT_TYPE,
        ).toUserTokens()

    override fun getAuthorizationUrl(callbackUrl: Url): Url =
        Url(
            URLBuilder().apply {
                protocol = URLProtocol.HTTPS
                host = io.ktor.http.Url(config.baseUrl.value).host
                encodedPath = "/oauth/authorize"
                parameters.append("response_type", "code")
                parameters.append("client_id", config.clientId.value)
                parameters.append("redirect_uri", callbackUrl.value)
            }.build().toString()
        )
}

