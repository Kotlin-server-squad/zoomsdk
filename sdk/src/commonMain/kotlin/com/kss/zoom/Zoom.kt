package com.kss.zoom

import com.kss.zoom.client.ApiClient
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
import com.kss.zoom.module.users.DefaultUsers
import com.kss.zoom.module.users.Users
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
            ?: return CallResult.Error.Other("No refresh token found for user $userId")
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
