package com.kss.zoom

import com.kss.zoom.auth.Authorization
import com.kss.zoom.auth.AuthorizationImpl
import com.kss.zoom.auth.UserTokens
import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.sdk.Meetings
import com.kss.zoom.sdk.MeetingsImpl
import com.kss.zoom.sdk.users.Users
import com.kss.zoom.sdk.users.UsersImpl
import io.ktor.client.*

class Zoom private constructor(private val authorization: Authorization, private val httpClient: HttpClient? = null) {

    fun auth(): Authorization = authorization

    fun meetings(tokens: UserTokens): Meetings =
        MeetingsImpl.create(tokens, httpClient)

    fun users(tokens: UserTokens): Users =
        UsersImpl.create(tokens, httpClient)

    companion object {
        fun create(clientId: String, clientSecret: String, httpClient: HttpClient? = null): Zoom {
            val authorization = AuthorizationImpl.create(
                AuthorizationConfig.create(clientId, clientSecret), httpClient
            )
            return Zoom(authorization, httpClient)
        }
    }
}
