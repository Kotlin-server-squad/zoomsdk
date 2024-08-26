package com.kss.zoom

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.call
import com.kss.zoom.common.extensions.coroutines.map
import com.kss.zoom.common.storage.InMemoryTokenStorage
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.common.tryCall
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.ZoomModuleConfig
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.auth.DefaultAuth
import com.kss.zoom.module.auth.model.AuthConfig
import com.kss.zoom.module.auth.model.UserTokens
import com.kss.zoom.module.meetings.DefaultMeetings
import com.kss.zoom.module.meetings.Meetings
import com.kss.zoom.module.meetings.model.GetRequest
import com.kss.zoom.module.users.DefaultUsers
import com.kss.zoom.module.users.Users
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

class Zoom private constructor(
    private val auth: Auth,
    private val tokenStorage: TokenStorage,
    private val meetings: Meetings,
    private val users: Users,
) {

    fun getAuthorizationUrl(callbackUrl: String): String =
        auth.getAuthorizationUrl(callbackUrl)

    suspend fun authorize(userId: String, code: String): CallResult<Unit> {
        return auth.authorize(code).map {
            // Save the user ID and tokens
            tokenStorage.saveUserTokens(userId, it)
        }
    }

    suspend fun reauthorize(userId: String): CallResult<Unit> {
        val refreshToken = tokenStorage.getRefreshToken(userId)
            ?: return CallResult.Error("No refresh token found for user $userId")
        return auth.reauthorize(refreshToken).map {
            tokenStorage.saveUserTokens(userId, it)
        }
    }

    suspend fun registerUser(userId: String, accessToken: String, refreshToken: String): CallResult<Unit> {
        return tryCall {
            // Save the user ID and tokens
            CallResult.Success(
                tokenStorage.saveUserTokens(userId, UserTokens(accessToken, refreshToken))
            )
        }

    }

    fun meetings(): Meetings = meetings
    fun users(): Users = users

    companion object {

        fun create(
            clientId: String,
            clientSecret: String,
            accountId: String,
            client: ApiClient = ApiClient.DEFAULT,
            tokenStorage: TokenStorage = InMemoryTokenStorage(),
            clock: Clock = Clock.System,
        ): Zoom {
            // Identify the account the SDK is working with
            // The account must have scopes necessary to perform all the operations required by the supported Zoom API calls
            val authConfig = AuthConfig(clientId, clientSecret, accountId)

            // Create Auth module for authentication / authorization
            val auth = DefaultAuth(authConfig, client)

            // Create modules to work with Zoom API
            val moduleConfig = ZoomModuleConfig()
            val meetings = DefaultMeetings(moduleConfig, auth, tokenStorage, clock, client)
            val users = DefaultUsers(moduleConfig, auth, tokenStorage, clock, client)

            // Create Zoom instance
            return Zoom(auth, tokenStorage, meetings, users)
        }
    }
}

// TODO move to README
fun main(): Unit = runBlocking {
    // Instantiate the SDK
    val zoom = Zoom.create("clientId", "clientSecret", "accountId")

    /**
     * OAuth2 flow: Requires user interaction
     */
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

    // Alternatively, we can register a user with their access and refresh tokens
    zoom.registerUser("userId4", "accessToken4", "refreshToken4")

    /**
     * Server-to-server flow: No user interaction required
     */
    // This is implicitly done by the SDK, if no user pair of access and refresh tokens is provided

    /**
     * Accessing the Zoom API
     */
    // Use the Zoom instance to access the Zoom API
    val meetings = zoom.meetings()
    val users = zoom.users()

    // Work with the Zoom API
    val meeting = call { meetings.get(GetRequest("userId1", "meetingId")) }
    println("Found meeting: $meeting")

    val user = call { users.get(com.kss.zoom.module.users.model.GetRequest("userId1")) }
    println("This is me: $user")
}
