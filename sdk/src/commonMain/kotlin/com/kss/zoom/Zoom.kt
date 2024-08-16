package com.kss.zoom

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.call
import com.kss.zoom.common.notBlank
import com.kss.zoom.common.storage.InMemoryTokenStorage
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.comap
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.auth.model.AuthConfig
import com.kss.zoom.module.meetings.Meetings
import com.kss.zoom.module.meetings.model.GetRequest
import com.kss.zoom.module.users.Users
import kotlinx.coroutines.runBlocking

class Zoom private constructor(
    private val auth: Auth,
    private val tokenStorage: TokenStorage,
    private val meetings: Meetings,
    private val users: Users,
) {

    fun getAuthorizationUrl(callbackUrl: String): String =
        auth.getAuthorizationUrl(callbackUrl)

    suspend fun authorize(userId: String, code: String): CallResult<Unit> {
        return auth.authorize(code).comap {
            // Save the user ID and tokens
            tokenStorage.saveTokens(userId, it)
        }
    }

    suspend fun reauthorize(userId: String): CallResult<Unit> {
        val refreshToken = tokenStorage.getRefreshToken(userId)
            ?: return CallResult.Error("No refresh token found for user $userId")
        return auth.reauthorize(refreshToken).comap {
            tokenStorage.saveTokens(userId, it)
        }
    }

    fun meetings(): Meetings = meetings
    fun users(): Users = users

    companion object {

        /**
         * TODO: The problem here is that it's tied to a single user!
         * Ideally we want a single Zoom instance that can be used by multiple (many: thousands) users.
         *
         * Solution:
         * - There needs to be a map: key is the user ID, value is the UserTokens, or maybe just the refresh token (memory space)
         */
        fun create(
            clientId: String,
            clientSecret: String,
            client: ApiClient = ApiClient.instance(),
            tokenStorage: TokenStorage = InMemoryTokenStorage(),
        ): Zoom {
            // Validate input
            clientId.notBlank("clientId")
            clientSecret.notBlank("clientSecret")

            val auth = Auth.create(AuthConfig(clientId, clientSecret), client)

            // Create modules
            val meetings = Meetings.create(auth, tokenStorage, client)
            val users = Users.create(auth, tokenStorage, client)

            // Create Zoom instance
            return Zoom(auth, tokenStorage, meetings, users)
        }
    }
}

// TODO move to README
fun main(): Unit = runBlocking {
    // Instantiate the SDK
    val zoom = Zoom.create("clientId", "clientSecret")

    // Helper method to get Zoom authorization URL
    val authUrl = zoom.getAuthorizationUrl("callbackUrl")
    println("Use $authUrl in the client code to obtain an authorization code")

    // Once OAuth is done, we can authorize the SDK on behalf of a particular user using the authorization code
    // We can authorize as many users as we want
    zoom.authorize("userId1", "code1")
    zoom.authorize("userId2", "code2")
    zoom.authorize("userId3", "code3")

    // We can refresh the authorization for a particular user
    zoom.reauthorize("userId1")

    // Use the Zoom instance to access the Zoom API
    val meetings = zoom.meetings()
    val users = zoom.users()

    // Work with the Zoom API
    val meeting = call { meetings.get(GetRequest("userId1", "meetingId")) }
    println("Found meeting: $meeting")

    val user = call { users.get("userId1") }
    println("This is me: $user")
}
