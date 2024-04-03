package com.kss.zoom.sdk.meetings

import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.sdk.common.*
import com.kss.zoom.sdk.common.model.Page
import com.kss.zoom.sdk.common.model.PagedQuery
import com.kss.zoom.sdk.meetings.model.Meeting
import com.kss.zoom.sdk.meetings.model.PaginationObject
import com.kss.zoom.sdk.meetings.model.ScheduledMeeting
import com.kss.zoom.sdk.meetings.model.api.CreateOneTimeMeetingRequest
import com.kss.zoom.sdk.meetings.model.api.MeetingResponse
import com.kss.zoom.sdk.meetings.model.api.toDomain
import com.kss.zoom.sdk.meetings.model.toDomain
import com.kss.zoom.sdk.users.model.UserId
import com.kss.zoom.sdk.webhooks.IWebhookVerifier
import com.kss.zoom.sdk.webhooks.model.Request
import com.kss.zoom.sdk.webhooks.model.api.*
import io.ktor.client.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

class Meetings private constructor(
    tokens: UserTokens? = null,
    client: WebClient,
    webhookVerifier: IWebhookVerifier? = null
) : ZoomModuleBase(tokens, client, webhookVerifier), IMeetings {
    companion object {
        fun create(
            tokens: UserTokens? = null,
            httpClient: HttpClient? = null,
            webhookVerifier: IWebhookVerifier? = null
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
        return webClient.post<MeetingResponse>(
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
        webClient.get<MeetingResponse>(
            url = "$ZOOM_API_URL/meetings/$meetingId",
            token = userTokens!!.accessToken.value
        ).map { it.toDomain() }

    override suspend fun delete(meetingId: Long): Result<Boolean> =
        webClient.delete(
            url = "$ZOOM_API_URL/meetings/$meetingId",
            token = userTokens!!.accessToken.value
        ).map { true }.also {
            logger.info { "Meeting $meetingId deleted" }
        }

    override suspend fun deleteAll(userId: UserId): Result<Boolean> {
        listScheduled(userId).map { page ->
            page.items.forEach { delete(it.id) }
            logger.info { "${page.items.size} out of ${page.totalRecords} meetings of user $userId deleted" }
            if (page.nextPageToken != null) {
                deleteAll(userId)
            }
        }
        return Result.success(true).also {
            logger.info { "All meetings of user $userId deleted" }
        }
    }

    override suspend fun listScheduled(userId: UserId, query: PagedQuery): Result<Page<ScheduledMeeting>> {
        val params = StringBuilder("type=scheduled&page_number=${query.pageNumber}&page_size=${query.pageSize}")
        query.filter?.let {
            params.append("&from=${it.startTime.toIsoString()}&to=${it.endTime.toIsoString()}")
        }
        query.nextPageToken?.let {
            params.append("&next_page_token=$it")
        }
        return webClient.get<PaginationObject>(
            url = "$ZOOM_API_URL/users/$userId/meetings?$params",
            token = userTokens!!.accessToken.value
        ).map { it.toDomain() }
    }

    override suspend fun onMeetingCreated(request: Request, action: (MeetingCreatedEvent) -> Unit) =
        handleEvent(request, SupportedEvents.Meeting.CREATED, action)

    override suspend fun onMeetingCreated(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingCreatedEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.CREATED, payload, timestamp, signature, action)

    override suspend fun onMeetingStarted(request: Request, action: (MeetingStartedEvent) -> Unit) =
        handleEvent(request, SupportedEvents.Meeting.STARTED, action)

    override suspend fun onMeetingStarted(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingStartedEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.STARTED, payload, timestamp, signature, action)

    override suspend fun onMeetingEnded(request: Request, action: (MeetingEndedEvent) -> Unit) =
        handleEvent(request, SupportedEvents.Meeting.ENDED, action)

    override suspend fun onMeetingEnded(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingEndedEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.ENDED, payload, timestamp, signature, action)

    override suspend fun onMeetingParticipantJoined(
        request: Request,
        action: (MeetingParticipantJoinedEvent) -> Unit
    ) = handleEvent(request, SupportedEvents.Meeting.PARTICIPANT_JOINED, action)

    override suspend fun onMeetingParticipantJoined(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingParticipantJoinedEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.PARTICIPANT_JOINED, payload, timestamp, signature, action)

    override suspend fun onMeetingParticipantLeft(
        request: Request,
        action: (MeetingParticipantLeftEvent) -> Unit
    ) = handleEvent(request, SupportedEvents.Meeting.PARTICIPANT_LEFT, action)

    override suspend fun onMeetingParticipantLeft(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingParticipantLeftEvent) -> Unit
    ): Result<Unit> =
        handleEvent(SupportedEvents.Meeting.PARTICIPANT_LEFT, payload, timestamp, signature, action)
}