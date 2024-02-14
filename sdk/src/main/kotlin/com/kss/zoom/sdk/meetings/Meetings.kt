package com.kss.zoom.sdk.meetings

import com.kss.zoom.auth.AccessToken
import com.kss.zoom.auth.Authorization
import com.kss.zoom.ZoomModule

interface Meetings : ZoomModule {

    /**
     * Schedule a meeting for the given user.
     * @param accessToken The access token of the user.
     * @param userId The user ID to schedule the meeting for.
     * @return The scheduled meeting.
     */
    suspend fun schedule(accessToken: AccessToken, userId: String): Result<ScheduledMeeting>
    suspend fun listScheduled(accessToken: AccessToken): Result<List<ScheduledMeeting>>
    suspend fun cancelScheduled(accessToken: AccessToken, meetingId: Long): Result<ScheduledMeeting>
}

class MeetingsImpl private constructor(private val auth: Authorization) : Meetings {
    override fun auth(): Authorization = auth

    companion object {
        fun create(authorization: Authorization): Meetings = MeetingsImpl(authorization)
    }

    override suspend fun schedule(accessToken: AccessToken, userId: String): Result<ScheduledMeeting> {
        TODO("Not yet implemented")
    }

    override suspend fun listScheduled(accessToken: AccessToken): Result<List<ScheduledMeeting>> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelScheduled(accessToken: AccessToken, meetingId: Long): Result<ScheduledMeeting> {
        TODO("Not yet implemented")
    }
}