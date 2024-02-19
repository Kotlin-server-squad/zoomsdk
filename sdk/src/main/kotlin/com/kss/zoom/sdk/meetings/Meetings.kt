package com.kss.zoom.sdk.meetings

import com.kss.zoom.sdk.ZoomModule
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.toWebClient
import io.ktor.client.*

interface Meetings : ZoomModule {

    /**
     * Create a meeting for the given user.
     * @param request Meeting parameters that will be used to create the meeting.
     * @return The created meeting.
     */
    suspend fun create(request: CreateMeetingRequest): Result<Meeting>
    suspend fun listScheduled(): Result<List<Meeting>>
    suspend fun cancelScheduled(meetingId: Long): Result<Meeting>
}

class MeetingsImpl private constructor(
    auth: UserTokens,
    client: WebClient
) : ZoomModuleBase(auth, client), Meetings {
    companion object {
        fun create(auth: UserTokens, httpClient: HttpClient? = null): Meetings =
            MeetingsImpl(auth, httpClient.toWebClient())
    }

    override suspend fun create(request: CreateMeetingRequest): Result<Meeting> {
        TODO("Not yet implemented")
    }

    override suspend fun listScheduled(): Result<List<Meeting>> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelScheduled(meetingId: Long): Result<Meeting> {
        TODO("Not yet implemented")
    }
}