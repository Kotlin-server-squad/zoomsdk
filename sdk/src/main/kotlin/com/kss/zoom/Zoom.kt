package com.kss.zoom

import com.kss.zoom.auth.Authorization
import com.kss.zoom.auth.IAuthorization
import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.meetings.IMeetings
import com.kss.zoom.meetings.Meetings
import io.ktor.client.*

class Zoom private constructor(private val authorization: IAuthorization, private val meetings: IMeetings) {

    fun auth(): IAuthorization = authorization

    fun meetings(): IMeetings = meetings

    companion object {
        fun create(clientId: String, clientSecret: String, httpClient: HttpClient? = null): Zoom {
            val authorization = Authorization.create(
                AuthorizationConfig.create(clientId, clientSecret), httpClient
            )
            val meetings = Meetings.create(authorization)

            return Zoom(
                authorization,
                meetings
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