package com.kss.zoom

import com.kss.zoom.auth.Authorization
import com.kss.zoom.auth.AuthorizationImpl
import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.sdk.meetings.Meetings
import com.kss.zoom.sdk.meetings.MeetingsImpl
import io.ktor.client.*

class Zoom private constructor(private val authorization: Authorization, private val meetings: Meetings) {

    fun auth(): Authorization = authorization

    fun meetings(): Meetings = meetings

    companion object {
        fun create(clientId: String, clientSecret: String, httpClient: HttpClient? = null): Zoom {
            val authorization = AuthorizationImpl.create(
                AuthorizationConfig.create(clientId, clientSecret), httpClient
            )
            val meetings = MeetingsImpl.create(authorization)

            return Zoom(
                authorization,
                meetings
                // Other modules will be added here
            )
        }

        fun meetings(clientId: String, clientSecret: String, httpClient: HttpClient? = null): Meetings {
            return MeetingsImpl.create(
                AuthorizationImpl.create(
                    AuthorizationConfig.create(clientId, clientSecret), httpClient
                )
            )
        }
    }
}
