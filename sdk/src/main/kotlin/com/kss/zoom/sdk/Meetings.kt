package com.kss.zoom.sdk

import com.kss.zoom.Page
import com.kss.zoom.PagedQuery
import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.model.ZOOM_API_URL
import com.kss.zoom.sdk.model.api.PaginationObject
import com.kss.zoom.sdk.model.api.meetings.CreateOneTimeMeetingRequest
import com.kss.zoom.sdk.model.api.meetings.MeetingResponse
import com.kss.zoom.sdk.model.api.meetings.events.*
import com.kss.zoom.sdk.model.api.meetings.toDomain
import com.kss.zoom.sdk.model.api.toDomain
import com.kss.zoom.sdk.model.domain.meetings.Meeting
import com.kss.zoom.sdk.model.domain.meetings.ScheduledMeeting
import com.kss.zoom.sdk.model.domain.users.UserId
import com.kss.zoom.toIsoString
import com.kss.zoom.toWebClient
import io.ktor.client.*
import io.ktor.server.application.*
import java.time.LocalDateTime
import java.util.*

interface Meetings : ZoomModule {

    /**
     * Create a one-time meeting for the given user.
     * @param topic The topic of the meeting.
     * @param startTime The time the meeting will start.
     * @param duration The duration of the meeting in minutes.
     * @param timezone The timezone of the meeting.
     * @return The created meeting.
     */
    suspend fun create(
        userId: UserId,
        topic: String,
        startTime: LocalDateTime,
        duration: Short,
        timezone: TimeZone
    ): Result<Meeting>

    /**
     * Get a meeting by id.
     * @param meetingId The id of the meeting to get.
     * @return The meeting.
     */
    suspend fun get(meetingId: Long): Result<Meeting>

    /**
     * Delete a meeting.
     * @param meetingId The id of the meeting to delete.
     * @return True if the meeting was deleted, false otherwise.
     */
    suspend fun delete(meetingId: Long): Result<Boolean>

    /**
     * Delete all meetings for the given user.
     * @param userId The id of the user to delete meetings for.
     * @return True if the meetings were deleted, false otherwise.
     */
    suspend fun deleteAll(userId: UserId): Result<Boolean>

    /**
     * List all scheduled meetings for the given user.
     * @param userId The id of the user to list meetings for.
     * @param query Limit and offset for the list of meetings.
     * @return A page of meetings.
     */
    suspend fun listScheduled(
        userId: UserId,
        query: PagedQuery = PagedQuery(pageNumber = 1, pageSize = 30)
    ): Result<Page<ScheduledMeeting>>

    suspend fun onMeetingCreated(call: ApplicationCall, action: (MeetingCreatedEvent) -> Unit)

    suspend fun onMeetingStarted(call: ApplicationCall, action: (MeetingStartedEvent) -> Unit)

    suspend fun onMeetingEnded(call: ApplicationCall, action: (MeetingEndedEvent) -> Unit)

    suspend fun onMeetingParticipantJoined(call: ApplicationCall, action: (MeetingParticipantJoinedEvent) -> Unit)

    suspend fun onMeetingParticipantLeft(call: ApplicationCall, action: (MeetingParticipantLeftEvent) -> Unit)
}

class MeetingsImpl private constructor(
    auth: UserTokens,
    client: WebClient,
    webhookVerifier: WebhookVerifier? = null
) : ZoomModuleBase(auth, client, webhookVerifier), Meetings {
    companion object {
        fun create(
            auth: UserTokens,
            httpClient: HttpClient? = null,
            webhookVerifier: WebhookVerifier? = null
        ): Meetings =
            MeetingsImpl(auth, httpClient.toWebClient(), webhookVerifier)
    }

    override suspend fun create(
        userId: UserId,
        topic: String,
        startTime: LocalDateTime,
        duration: Short,
        timezone: TimeZone
    ): Result<Meeting> {
        assert(duration > 0) { "Duration must be greater than 0" }
        return client.post<MeetingResponse>(
            url = "$ZOOM_API_URL/users/$userId/meetings",
            token = userTokens!!.accessToken.value,
            contentType = WebClient.JSON_CONTENT_TYPE,
            body = CreateOneTimeMeetingRequest(
                topic = topic,
                type = 2,
                startTime = startTime.toIsoString(),
                duration = duration,
                timezone = timezone.id
            )
        ).map { it.toDomain() }
    }

    override suspend fun get(meetingId: Long): Result<Meeting> =
        client.get<MeetingResponse>(
            url = "$ZOOM_API_URL/meetings/$meetingId",
            token = userTokens!!.accessToken.value
        ).map { it.toDomain() }

    override suspend fun delete(meetingId: Long): Result<Boolean> =
        client.delete(
            url = "$ZOOM_API_URL/meetings/$meetingId",
            token = userTokens!!.accessToken.value
        ).map { true }.also {
            logger.info("Meeting {} deleted", meetingId)
        }

    override suspend fun deleteAll(userId: UserId): Result<Boolean> {
        listScheduled(userId).map { page ->
            page.items.forEach { delete(it.id) }
            logger.info("{} out of {} meetings of user {} deleted", page.items.size, page.totalRecords, userId)
            if (page.nextPageToken != null) {
                deleteAll(userId)
            }
        }
        return Result.success(true).also {
            logger.info("All meetings of user {} deleted", userId)
        }
    }

    override suspend fun listScheduled(userId: UserId, query: PagedQuery): Result<Page<ScheduledMeeting>> {
        val params = StringBuilder("?type=scheduled&page_number=${query.pageNumber}&page_size=${query.pageSize}")
        query.filter?.let {
            params.append("&from=${it.startDate.toIsoString()}&to=${it.endDate.toIsoString()}")
        }
        query.nextPageToken?.let {
            params.append("&next_page_token=$it")
        }
        return client.get<PaginationObject>(
            url = "$ZOOM_API_URL/users/$userId/meetings?$params",
            token = userTokens!!.accessToken.value
        ).map { it.toDomain() }
    }

    override suspend fun onMeetingCreated(call: ApplicationCall, action: (MeetingCreatedEvent) -> Unit) =
        handleEvent(call, SupportedEvents.Meeting.CREATED, action)

    override suspend fun onMeetingStarted(call: ApplicationCall, action: (MeetingStartedEvent) -> Unit) =
        handleEvent(call, SupportedEvents.Meeting.STARTED, action)

    override suspend fun onMeetingEnded(call: ApplicationCall, action: (MeetingEndedEvent) -> Unit) =
        handleEvent(call, SupportedEvents.Meeting.ENDED, action)

    override suspend fun onMeetingParticipantJoined(
        call: ApplicationCall,
        action: (MeetingParticipantJoinedEvent) -> Unit
    ) = handleEvent(call, SupportedEvents.Meeting.PARTICIPANT_JOINED, action)

    override suspend fun onMeetingParticipantLeft(
        call: ApplicationCall,
        action: (MeetingParticipantLeftEvent) -> Unit
    ) = handleEvent(call, SupportedEvents.Meeting.PARTICIPANT_LEFT, action)
}

