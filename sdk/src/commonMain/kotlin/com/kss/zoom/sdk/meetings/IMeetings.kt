package com.kss.zoom.sdk.meetings

import com.kss.zoom.sdk.IZoomModule
import com.kss.zoom.sdk.common.model.Page
import com.kss.zoom.sdk.common.model.PagedQuery
import com.kss.zoom.sdk.meetings.model.Meeting
import com.kss.zoom.sdk.meetings.model.ScheduledMeeting
import com.kss.zoom.sdk.users.model.UserId
import com.kss.zoom.sdk.webhooks.model.Request
import com.kss.zoom.sdk.webhooks.model.api.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

interface IMeetings : IZoomModule {
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

    suspend fun onMeetingCreated(request: Request, action: (MeetingCreatedEvent) -> Unit): Result<Unit>

    suspend fun onMeetingCreated(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingCreatedEvent) -> Unit
    ): Result<Unit>

    suspend fun onMeetingStarted(request: Request, action: (MeetingStartedEvent) -> Unit): Result<Unit>

    suspend fun onMeetingStarted(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingStartedEvent) -> Unit
    ): Result<Unit>

    suspend fun onMeetingEnded(request: Request, action: (MeetingEndedEvent) -> Unit): Result<Unit>

    suspend fun onMeetingEnded(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingEndedEvent) -> Unit
    ): Result<Unit>

    suspend fun onMeetingParticipantJoined(
        request: Request,
        action: (MeetingParticipantJoinedEvent) -> Unit
    ): Result<Unit>

    suspend fun onMeetingParticipantJoined(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingParticipantJoinedEvent) -> Unit
    ): Result<Unit>

    suspend fun onMeetingParticipantLeft(
        request: Request,
        action: (MeetingParticipantLeftEvent) -> Unit
    ): Result<Unit>

    suspend fun onMeetingParticipantLeft(
        payload: String,
        timestamp: Long,
        signature: String,
        action: (MeetingParticipantLeftEvent) -> Unit
    ): Result<Unit>
}