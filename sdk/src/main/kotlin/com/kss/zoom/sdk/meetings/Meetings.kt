package com.kss.zoom.sdk.meetings

import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.webhooks.WebhookVerifier
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.sdk.common.SupportedEvents
import com.kss.zoom.sdk.common.ZOOM_API_URL
import com.kss.zoom.sdk.common.model.Page
import com.kss.zoom.sdk.common.model.PagedQuery
import com.kss.zoom.sdk.common.model.api.PaginationObject
import com.kss.zoom.sdk.common.model.api.toDomain
import com.kss.zoom.sdk.common.toIsoString
import com.kss.zoom.sdk.common.toWebClient
import com.kss.zoom.sdk.meetings.model.Meeting
import com.kss.zoom.sdk.meetings.model.ScheduledMeeting
import com.kss.zoom.sdk.meetings.model.api.CreateOneTimeMeetingRequest
import com.kss.zoom.sdk.meetings.model.api.MeetingResponse
import com.kss.zoom.sdk.meetings.model.api.events.*
import com.kss.zoom.sdk.meetings.model.api.toDomain
import com.kss.zoom.sdk.users.model.UserId
import io.ktor.client.*
import io.ktor.server.application.*
import java.time.LocalDateTime
import java.util.*

class Meetings private constructor(
    tokens: UserTokens? = null,
    client: WebClient,
    webhookVerifier: WebhookVerifier? = null
) : ZoomModuleBase(tokens, client, webhookVerifier), IMeetings {
    companion object {
        fun create(
            tokens: UserTokens? = null,
            httpClient: HttpClient? = null,
            webhookVerifier: WebhookVerifier? = null
        ): IMeetings =
            Meetings(tokens, httpClient.toWebClient(), webhookVerifier)
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
        val params = StringBuilder("type=scheduled&page_number=${query.pageNumber}&page_size=${query.pageSize}")
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

    override suspend fun onMeetingCreated(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingCreatedEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.CREATED, payload, timestamp, signature, action)

    override suspend fun onMeetingStarted(call: ApplicationCall, action: (MeetingStartedEvent) -> Unit) =
        handleEvent(call, SupportedEvents.Meeting.STARTED, action)

    override suspend fun onMeetingStarted(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingStartedEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.STARTED, payload, timestamp, signature, action)

    override suspend fun onMeetingEnded(call: ApplicationCall, action: (MeetingEndedEvent) -> Unit) =
        handleEvent(call, SupportedEvents.Meeting.ENDED, action)

    override suspend fun onMeetingEnded(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingEndedEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.ENDED, payload, timestamp, signature, action)

    override suspend fun onMeetingParticipantJoined(
        call: ApplicationCall,
        action: (MeetingParticipantJoinedEvent) -> Unit
    ) = handleEvent(call, SupportedEvents.Meeting.PARTICIPANT_JOINED, action)

    override suspend fun onMeetingParticipantJoined(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingParticipantJoinedEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.PARTICIPANT_JOINED, payload, timestamp, signature, action)

    override suspend fun onMeetingParticipantLeft(
        call: ApplicationCall,
        action: (MeetingParticipantLeftEvent) -> Unit
    ) = handleEvent(call, SupportedEvents.Meeting.PARTICIPANT_LEFT, action)

    override suspend fun onMeetingParticipantLeft(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingParticipantLeftEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.PARTICIPANT_LEFT, payload, timestamp, signature, action)
}
