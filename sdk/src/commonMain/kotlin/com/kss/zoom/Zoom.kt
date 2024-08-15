package com.kss.zoom

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.call
import com.kss.zoom.common.notBlank
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.map
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.auth.model.AuthConfig
import com.kss.zoom.module.meetings.Meetings
import com.kss.zoom.module.users.Users
import kotlinx.coroutines.runBlocking

class Zoom private constructor(
    private val auth: Auth,
    private val meetings: Meetings,
    private val users: Users,
) {

    suspend fun refreshTokens(): CallResult<Zoom> {
        return auth.refreshTokens().map { this }
    }

    fun meetings(): Meetings = meetings
    fun users(): Users = users

    companion object {
        fun create(
            clientId: String,
            clientSecret: String,
            authCode: String,
            client: ApiClient = ApiClient.instance(),
        ): Zoom {
            // Validate input
            clientId.notBlank("clientId")
            clientSecret.notBlank("clientSecret")
            authCode.notBlank("authCode")

            // Authorize the app on behalf of the user
            val auth = Auth.create(AuthConfig(clientId, clientSecret), client)
            runBlocking {
                call { auth.authorize(authCode) }
            }

            // Create modules
            val meetings = Meetings.create(auth, client)
            val users = Users.create(auth, client)

            // Create Zoom instance
            return Zoom(auth, meetings, users)
        }

        fun getAuthorizationUrl(clientId: String, callbackUrl: String): String =
            Auth.getAuthorizationUrl(clientId, callbackUrl)
    }
}

// TODO move to README
fun main() = runBlocking {
    // Helper method to get Zoom authorization URL
    val authUrl = Zoom.getAuthorizationUrl("clientId", "callbackUrl")

    // Once OAuth is done and we have the auth code, we can create a Zoom instance
    val zoom = Zoom.create("clientId", "clientSecret", "authCode")

    // Use the Zoom instance to access the Zoom API
    val meetings = zoom.meetings()
    val users = zoom.users()

    // Work with the Zoom API
    val meeting = call { meetings.get("meetingId") }
    val me = call { users.me() }
}
