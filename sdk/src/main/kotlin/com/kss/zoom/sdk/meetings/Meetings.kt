package com.kss.zoom.sdk.meetings

import com.kss.zoom.sdk.ZoomModule
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.toWebClient
import io.ktor.client.*

interface Meetings : ZoomModule {

    /**
     * Schedule a meeting for the given user.
     * @param userId The user ID to schedule the meeting for.
     * @return The scheduled meeting.
     */
    suspend fun schedule(userId: String): Result<ScheduledMeeting>
    suspend fun listScheduled(): Result<List<ScheduledMeeting>>
    suspend fun cancelScheduled(meetingId: Long): Result<ScheduledMeeting>
}

class MeetingsImpl private constructor(
    auth: UserTokens,
    client: WebClient
) : ZoomModuleBase(auth, client), Meetings {
    companion object {
        fun create(auth: UserTokens, httpClient: HttpClient? = null): Meetings =
            MeetingsImpl(auth, httpClient.toWebClient())
    }

    override suspend fun schedule(userId: String): Result<ScheduledMeeting> {
        TODO("Not yet implemented")
    }

    override suspend fun listScheduled(): Result<List<ScheduledMeeting>> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelScheduled(meetingId: Long): Result<ScheduledMeeting> {
        TODO("Not yet implemented")
    }
}