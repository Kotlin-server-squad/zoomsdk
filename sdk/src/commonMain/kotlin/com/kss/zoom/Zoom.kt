package com.kss.zoom

import com.kss.zoom.auth.Authorization
import com.kss.zoom.auth.IAuthorization
import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.sdk.common.toWebClient
import com.kss.zoom.sdk.meetings.IMeetings
import com.kss.zoom.sdk.meetings.Meetings
import com.kss.zoom.sdk.users.IUsers
import com.kss.zoom.sdk.users.Users
import com.kss.zoom.sdk.webhooks.WebhookVerifier
import io.ktor.client.*

class Zoom private constructor(
    private val authorization: IAuthorization,
    private val httpClient: HttpClient? = null,
    private val webhookVerifier: WebhookVerifier? = null
) {

    fun auth(): IAuthorization = authorization

    fun meetings(tokens: UserTokens? = null): IMeetings =
        Meetings.create(tokens, httpClient, webhookVerifier)

    fun users(tokens: UserTokens? = null): IUsers =
        Users(tokens, httpClient)

    companion object {
        fun create(
            clientId: String,
            clientSecret: String,
            verificationToken: String? = null,
            httpClient: HttpClient? = null
        ): Zoom {
            val authorization = Authorization(
                AuthorizationConfig.create(clientId, clientSecret), httpClient.toWebClient()
            )
            val webhookVerifier = verificationToken?.let { WebhookVerifier(it) }
            return Zoom(authorization, httpClient, webhookVerifier)
        }
    }
}
