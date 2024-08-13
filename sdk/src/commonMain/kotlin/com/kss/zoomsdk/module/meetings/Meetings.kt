package com.kss.zoomsdk.module.meetings

import com.kss.zoomsdk.Auth
import com.kss.zoomsdk.assert
import com.kss.zoomsdk.client.ApiClient
import com.kss.zoomsdk.model.CallResult
import com.kss.zoomsdk.model.Page
import com.kss.zoomsdk.model.PageRequest
import com.kss.zoomsdk.model.map
import com.kss.zoomsdk.module.meetings.model.CreateRequest
import com.kss.zoomsdk.module.meetings.model.Meeting
import com.kss.zoomsdk.module.meetings.model.UpdateRequest
import com.kss.zoomsdk.module.meetings.model.api.MeetingRequest
import com.kss.zoomsdk.module.meetings.model.api.MeetingResponse
import com.kss.zoomsdk.module.meetings.model.api.toModel

interface Meetings {
    companion object {
        fun instance(): Meetings {
            TODO()
        }

    }

    suspend fun create(request: CreateRequest): CallResult<Meeting>
    suspend fun update(request: UpdateRequest): CallResult<Meeting>
    suspend fun delete(meetingId: String): CallResult<Meeting>
    suspend fun deleteAll(userId: String): CallResult<Int>
    suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<Meeting>>
}

private class DefaultMeetings(private val client: ApiClient, private val auth: Auth) : Meetings {
    override suspend fun create(request: CreateRequest): CallResult<Meeting> {
        assert(request.duration > 0) { "Duration must be greater than 0" }
        return client.post<MeetingResponse>(
            path = "/meetings",
            token = auth.accessToken(),
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

    override suspend fun delete(meetingId: String): CallResult<Meeting> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll(userId: String): CallResult<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<Meeting>> {
        TODO("Not yet implemented")
    }
}

