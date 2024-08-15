package com.kss.zoom.module.meetings

import com.kss.zoom.client.ApiClient
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.Page
import com.kss.zoom.model.PageRequest
import com.kss.zoom.model.map
import com.kss.zoom.module.ZoomModule
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.meetings.model.CreateRequest
import com.kss.zoom.module.meetings.model.Meeting
import com.kss.zoom.module.meetings.model.UpdateRequest
import com.kss.zoom.module.meetings.model.api.MeetingRequest
import com.kss.zoom.module.meetings.model.api.MeetingResponse
import com.kss.zoom.module.meetings.model.api.toModel

interface Meetings : ZoomModule {
    companion object {
        fun create(auth: Auth, apiClient: ApiClient = ApiClient.instance()): Meetings {
            return DefaultMeetings(auth, apiClient)
        }
    }

    suspend fun create(request: CreateRequest): CallResult<Meeting>
    suspend fun update(request: UpdateRequest): CallResult<Meeting>
    suspend fun get(meetingId: String): CallResult<Meeting>
    suspend fun delete(meetingId: String): CallResult<Meeting>
    suspend fun deleteAll(userId: String): CallResult<Int>
    suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<Meeting>>
}

private class DefaultMeetings(private val auth: Auth, private val client: ApiClient) : Meetings {
    override suspend fun create(request: CreateRequest): CallResult<Meeting> = withAccessToken(auth) { token ->
        client.post<MeetingResponse>(
            path = "/meetings",
            token = token,
            contentType = "application/json",
            body = MeetingRequest(
                topic = request.topic,
                type = 2,
                startTime = request.startTime.toString(),
                duration = request.duration,
                timeZone = request.timezone
            )
        ).map { it.toModel() }
    }

    override suspend fun update(request: UpdateRequest): CallResult<Meeting> {
        TODO("Not yet implemented")
    }

    override suspend fun get(meetingId: String): CallResult<Meeting> = withAccessToken(auth) { token ->
        client.get<MeetingResponse>("meetings/$meetingId", token).map { it.toModel() }
    }

    override suspend fun delete(meetingId: String): CallResult<Meeting> = withAccessToken(auth) { token ->
        client.delete<MeetingResponse>("meetings/$meetingId", token).map { it.toModel() }
    }

    override suspend fun deleteAll(userId: String): CallResult<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<Meeting>> {
        TODO("Not yet implemented")
    }
}

