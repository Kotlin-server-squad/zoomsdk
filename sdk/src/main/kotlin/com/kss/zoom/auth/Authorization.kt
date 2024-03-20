package com.kss.zoom.auth

import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.auth.model.*
import com.kss.zoom.auth.model.api.ServerAuthorizationResponse
import com.kss.zoom.auth.model.api.UserAuthorizationResponse
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.common.toWebClient
import io.ktor.client.*
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Authorization(private val config: AuthorizationConfig, private val client: WebClient) : IAuthorization {

    companion object {
        fun create(config: AuthorizationConfig, httpClient: HttpClient? = null): Authorization {
            return Authorization(config, httpClient.toWebClient())
        }
    }

    private val oauthTokenUrl = "${config.baseUrl}/oauth/token"

    override suspend fun generateAccessToken(accountId: AccountId): Result<AccessToken> =
        client.post<ServerAuthorizationResponse>(
            url = oauthTokenUrl,
            clientId = config.clientId.value,
            clientSecret = config.clientSecret.value,
            contentType = WebClient.FORM_URL_ENCODED_CONTENT_TYPE,
            body = "grant_type=account_credentials&account_id=${accountId.value}"
        ).toAccessToken()


    override suspend fun authorizeUser(code: AuthorizationCode): Result<UserTokens> =
        client.post<UserAuthorizationResponse>(
            url = "$oauthTokenUrl?grant_type=authorization_code&code=${code.value}&redirect_uri=http://localhost:8080/callback",
            clientId = config.clientId.value,
            clientSecret = config.clientSecret.value,
            contentType = WebClient.FORM_URL_ENCODED_CONTENT_TYPE,
            body = null
        ).toUserTokens()

    override suspend fun refreshUserAuthorization(refreshToken: RefreshToken): Result<UserTokens> =
        client.post<UserAuthorizationResponse>(
            url = oauthTokenUrl,
            body = "grant_type=refresh_token&refresh_token=${refreshToken.value}",
            contentType = WebClient.FORM_URL_ENCODED_CONTENT_TYPE,
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

fun Result<UserAuthorizationResponse>.toUserTokens(): Result<UserTokens> =
    this.map { it.toUserTokens() }

fun Result<ServerAuthorizationResponse>.toAccessToken(): Result<AccessToken> =
    this.map { it.toAccessToken() }
