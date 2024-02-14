package com.kss.zoom.sdk.meetings

import com.kss.zoom.ZoomModule
import com.kss.zoom.ZoomModuleBase
import com.kss.zoom.auth.Authorization

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

class MeetingsImpl private constructor(auth: Authorization) : ZoomModuleBase(auth), Meetings {
    companion object {
        fun create(authorization: Authorization): Meetings = MeetingsImpl(authorization)
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