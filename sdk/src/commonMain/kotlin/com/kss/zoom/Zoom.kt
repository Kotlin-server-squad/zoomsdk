package com.kss.zoom

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.extensions.coroutines.map
import com.kss.zoom.common.storage.InMemoryTokenStorage
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.common.tryCall
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.DynamicProperty.Companion.nullable
import com.kss.zoom.model.DynamicProperty.Companion.required
import com.kss.zoom.module.ZoomModuleConfig
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.auth.DefaultAuth
import com.kss.zoom.module.auth.model.AuthConfig
import com.kss.zoom.module.auth.model.UserTokens
import com.kss.zoom.module.meetings.DefaultMeetings
import com.kss.zoom.module.meetings.Meetings
import com.kss.zoom.module.meetings.model.MeetingSerializer
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

// TODO Add this to the documentation about dynamic properties
fun main() {
    // Let's assume this block is a custom event handler for a Zoom webhook. These are some of the custom fields that can be added to the base model
    val name = required("name", "Unknown")
    val scheduleType = required("schedule_type", 1)
    val description = nullable<String>("description")
    val metadata = required<Map<String, String>>("metadata", emptyMap())

    // Register a custom JSON serializer - do this once in the application
    val meetingResponseSerializer = MeetingSerializer(name, scheduleType, description, metadata)

    // Incoming JSON from the webhook
    val json = """
        {
            "id": "123",
            "uuid": "uuid",
            "topic": "topic",
            "duration": 60,
            "host_id": "hostId",
            "created_at": "2024-01-01T00:00:00Z",
            "start_time": "2024-01-01T00:00:00Z",
            "timezone": "timezone",
            "join_url": "joinUrl",
            "name": "Meeting",
            "description": "My test event",
            "schedule_type": 2,
            "metadata": {
                "user_id": "user123",
                "registered_at": "2024-01-01T00:00:00Z"
            }
        }
    """.trimIndent()

    val meetingModel = meetingResponseSerializer.toModel(json)
    println(meetingModel)
    println("Custom field 'name': ${meetingModel.context[name]}")
    println("Custom field 'scheduleType': ${meetingModel.context[scheduleType]}")

    /**
     *     // Let's say this is some deserialized JSON object in the base form, with all expected (statically typed) fields
     *     val meeting = Meeting(
     *         id = "id",
     *         uuid = "uuid",
     *         topic = "topic",
     *         duration = 60,
     *         hostId = "hostId",
     *         createdAt = 0,
     *         startTime = 0,
     *         timezone = "timezone",
     *         joinUrl = "joinUrl",
     *
     *         // Now we add the dynamic properties
     *         context = context {
     *             name - "Meeting"
     *             scheduleType - 2  // Let's say that this is missing in the input JSON, it should fallback to the default value
     *             description - "My test event"
     *             metadata - mapOf("userId" to "user123")
     *         }
     *     )
     */

}
