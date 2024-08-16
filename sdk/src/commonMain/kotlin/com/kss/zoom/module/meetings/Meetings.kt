package com.kss.zoom.module.meetings

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.Page
import com.kss.zoom.model.PageRequest
import com.kss.zoom.model.map
import com.kss.zoom.module.BaseZoomModule
import com.kss.zoom.module.ZoomModule
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.meetings.model.*
import com.kss.zoom.module.meetings.model.api.MeetingRequest
import com.kss.zoom.module.meetings.model.api.MeetingResponse
import com.kss.zoom.module.meetings.model.api.toModel

interface Meetings : ZoomModule {
    companion object {
        fun create(auth: Auth, tokenStorage: TokenStorage, apiClient: ApiClient = ApiClient.instance()): Meetings {
            return DefaultMeetings(auth, tokenStorage, apiClient)
        }
    }

    suspend fun create(request: CreateRequest): CallResult<Meeting>
    suspend fun update(request: UpdateRequest): CallResult<Meeting>
    suspend fun get(request: GetRequest): CallResult<Meeting>
    suspend fun delete(request: DeleteRequest): CallResult<Meeting>
    suspend fun deleteAll(userId: String): CallResult<Int>
    suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<Meeting>>
}

private class DefaultMeetings(auth: Auth, tokenStorage: TokenStorage, private val client: ApiClient) :
    BaseZoomModule(auth, tokenStorage), Meetings {
    override suspend fun create(request: CreateRequest): CallResult<Meeting> =
        withAccessToken(request.userId) { token ->
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

    override suspend fun get(request: GetRequest): CallResult<Meeting> = withAccessToken(request.userId) { token ->
        client.get<MeetingResponse>("meetings/${request.meetingId}", token).map { it.toModel() }
    }

    override suspend fun delete(request: DeleteRequest): CallResult<Meeting> =
        withAccessToken(request.meetingId) { token ->
            client.delete<MeetingResponse>("meetings/${request.meetingId}", token).map { it.toModel() }
        }

    override suspend fun deleteAll(userId: String): CallResult<Int> = withAccessToken(userId) { token ->
        TODO("Not yet implemented")
    }

    override suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<Meeting>> =
        withAccessToken(userId) { token ->
            TODO("Not yet implemented")
        }
}

