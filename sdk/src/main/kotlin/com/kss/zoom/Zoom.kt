package com.kss.zoom

import com.kss.zoom.auth.Authorization
import com.kss.zoom.auth.IAuthorization
import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.meetings.IMeetings
import com.kss.zoom.meetings.Meetings
import io.ktor.client.*

class Zoom private constructor(private val authorization: IAuthorization) {

    fun auth(): IAuthorization = authorization

    companion object {
        fun create(clientId: String, clientSecret: String, httpClient: HttpClient? = null): Zoom {
            return Zoom(
                authorization = Authorization.create(
                    AuthorizationConfig.create(clientId, clientSecret), httpClient
                )
                // Other modules will be added here
            )
        }

        fun meetings(clientId: String, clientSecret: String, httpClient: HttpClient? = null): IMeetings {
            return Meetings.create(
                Authorization.create(
                    AuthorizationConfig.create(clientId, clientSecret), httpClient
                )
            )
        }
    }
}