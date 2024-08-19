package com.kss.zoom.module.meetings

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.extensions.coroutines.flatMap
import com.kss.zoom.common.extensions.map
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.pagination.Page
import com.kss.zoom.model.pagination.PageRequest
import com.kss.zoom.module.ZoomModuleBase
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.meetings.model.*
import com.kss.zoom.module.meetings.model.api.*
import com.kss.zoom.module.meetings.model.pagination.filter.MeetingType
import com.kss.zoom.module.meetings.model.pagination.filter.MeetingTypeFilter
import com.kss.zoom.common.extensions.coroutines.map as coMap

class DefaultMeetings(auth: Auth, tokenStorage: TokenStorage, private val client: ApiClient) :
    ZoomModuleBase(auth, tokenStorage), Meetings {
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
                    timezone = request.timezone
                )
            ).map { it.toModel() }
        }

    override suspend fun update(request: UpdateRequest): CallResult<Meeting> =
        withAccessToken(request.userId) { token ->
            client.patch<Unit>(
                path = "/meetings/${request.meetingId}",
                token = token,
                contentType = "application/json",
                body = request.toApi()
            ).flatMap {
                get(
                    GetRequest(
                        userId = request.userId,
                        meetingId = request.meetingId
                    )
                )
            }
        }

    override suspend fun get(request: GetRequest): CallResult<Meeting> = withAccessToken(request.userId) { token ->
        client.get<MeetingResponse>("meetings/${request.meetingId}", token).map { it.toModel() }
    }

    override suspend fun delete(request: DeleteRequest): CallResult<Meeting> =
        withAccessToken(request.userId) { token ->
            client.delete<MeetingResponse>("meetings/${request.meetingId}", token).map { it.toModel() }
        }

    override suspend fun deleteAll(request: DeleteAllRequest): CallResult<Int> =
        deleteAllInternal(request)

    override suspend fun list(request: ListRequest): CallResult<Page<Meeting>> =
        withAccessToken(request.userId) { token ->
            val params =
                StringBuilder("type=scheduled&page_number=${request.pageRequest.index}&page_size=${request.pageRequest.size}")
            request.pageRequest.filters.forEach { params.append("&${it.toQueryString()}") }
            request.pageRequest.nextPageToken?.let { params.append("&next_page_token=$it") }
            client.get<PaginationObject>(
                path = "/users/${request.userId}/meetings?$params",
                token = token
            ).map { it.toModel() }
        }

    private suspend fun deleteAllInternal(request: DeleteAllRequest, count: Int = 0): CallResult<Int> {
        return list(
            ListRequest(
                userId = request.userId,
                pageRequest = PageRequest(
                    filters = listOf(
                        MeetingTypeFilter(MeetingType.Scheduled)
                    ),
                    nextPageToken = request.nextPageToken
                )
            )
        ).coMap { page ->
            page.items.forEach {
                delete(DeleteRequest(request.userId, it.id))
            }
            page.nextPageToken?.let {
                deleteAllInternal(
                    request = request.copy(nextPageToken = it),
                    count = count + page.items.size
                )
            }
            count + page.items.size
        }
    }
}
