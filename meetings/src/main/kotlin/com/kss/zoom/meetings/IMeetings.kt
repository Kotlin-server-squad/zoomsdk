package com.kss.zoom.meetings

import com.kss.zoom.auth.AccessToken
import com.kss.zoom.auth.IAuthorization

interface IMeetings {
    fun auth(): IAuthorization

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

class Meetings private constructor(private val authorization: IAuthorization) : IMeetings {
    override fun auth(): IAuthorization = authorization

    companion object {
        fun create(authorization: IAuthorization): IMeetings = Meetings(authorization)
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