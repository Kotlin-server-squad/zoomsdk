package com.kss.zoom.sdk.meetings

import com.kss.zoom.PagedQuery
import com.kss.zoom.PagedResponse
import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.ZOOM_API_URL
import com.kss.zoom.sdk.ZoomModule
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.sdk.users.UserId
import com.kss.zoom.toIsoString
import com.kss.zoom.toWebClient
import io.ktor.client.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZonedDateTime
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
     * List all scheduled meetings for the given user.
     * @param userId The id of the user to list meetings for.
     * @param query Limit and offset for the list of meetings.
     * @return A page of meetings.
     */
    suspend fun listScheduled(
        userId: UserId,
        query: PagedQuery = PagedQuery(pageNumber = 1, pageSize = 30)
    ): Result<PagedResponse<Meeting>>
}

class MeetingsImpl private constructor(
    auth: UserTokens,
    client: WebClient
) : ZoomModuleBase(auth, client), Meetings {
    companion object {
        fun create(auth: UserTokens, httpClient: HttpClient? = null): Meetings =
            MeetingsImpl(auth, httpClient.toWebClient())
    }

    override suspend fun create(
        userId: UserId,
        topic: String,
        startTime: LocalDateTime,
        duration: Short,
        timezone: TimeZone
    ): Result<Meeting> {
        assert(duration > 0) { "Duration must be greater than 0" }
        return client.post<Http.MeetingResponse>(
            url = "$ZOOM_API_URL/users/$userId/meetings",
            token = userTokens!!.accessToken.value,
            contentType = WebClient.JSON_CONTENT_TYPE,
            body = Http.CreateOneTimeMeetingRequest(
                topic = topic,
                type = 2,
                startTime = startTime.toIsoString(),
                duration = duration,
                timezone = timezone.id
            )
        ).map { it.toModel() }
    }

    override suspend fun get(meetingId: Long): Result<Meeting> =
        client.get<Http.MeetingResponse>(
            url = "$ZOOM_API_URL/meetings/$meetingId",
            token = userTokens!!.accessToken.value
        ).map { it.toModel() }

    override suspend fun delete(meetingId: Long): Result<Boolean> =
        client.delete(
            url = "$ZOOM_API_URL/meetings/$meetingId",
            token = userTokens!!.accessToken.value
        ).map { true }

    override suspend fun listScheduled(userId: UserId, query: PagedQuery): Result<PagedResponse<Meeting>> {
        val params = StringBuilder("?type=scheduled&page_number=${query.pageNumber}&page_size=${query.pageSize}")
        query.filter?.let {
            params.append("&from=${it.startDate.toIsoString()}&to=${it.endDate.toIsoString()}")
        }
        query.nextPageToken?.let {
            params.append("&next_page_token=$it")
        }
        return client.get<Http.PaginationObject>(
            url = "$ZOOM_API_URL/users/$userId/meetings?$params",
            token = userTokens!!.accessToken.value
        ).map { it.toModel() }
    }
}

private fun Http.PaginationObject.toModel(): PagedResponse<Meeting> =
    PagedResponse(
        items = this.meetings.map { it.toModel() },
        nextPageToken = this.nextPageToken
    )

private fun Http.MeetingResponse.toModel(): Meeting =
    Meeting(
        id = id,
        uuid = uuid,
        topic = topic,
        host = MeetingHost(
            id = hostId,
            email = hostEmail
        ),
        startTime = ZonedDateTime.parse(startTime).toLocalDateTime(),
        duration = duration,
        joinUrl = joinUrl,
        startUrl = startUrl,
        password = password,
        createdAt = ZonedDateTime.parse(createdAt).toInstant(),
        timeZone = TimeZone.getTimeZone(timezone),
    )

private fun Http.ScheduledMeeting.toModel(): Meeting =
    Meeting(
        id = id,
        uuid = "",
        topic = topic ?: "",
        host = MeetingHost(
            id = hostId ?: "",
            email = ""
        ),
        startTime = LocalDateTime.now(),
        duration = duration ?: 0,
        joinUrl = "",
        startUrl = "",
        password = "",
        createdAt = ZonedDateTime.parse(createdAt).toInstant(),
        timeZone = TimeZone.getDefault(),
    )

private object Http {
    @Serializable
    data class CreateOneTimeMeetingRequest(
        val topic: String,
        val type: Short,
        @SerialName("start_time") val startTime: String? = null,
        val duration: Short? = null,
        val timezone: String? = null
    )

    @Serializable
    data class MeetingResponse(
        val id: Long,
        val uuid: String,
        val topic: String,
        val duration: Short,
        @SerialName("host_id") val hostId: String,
        @SerialName("host_email") val hostEmail: String,
        val status: String,
        @SerialName("start_time") val startTime: String,
        val timezone: String,
        @SerialName("created_at") val createdAt: String,
        @SerialName("start_url") val startUrl: String,
        @SerialName("join_url") val joinUrl: String,
        val password: String
    )

    @Serializable
    data class ScheduledMeeting(
        val id: Long,
        val topic: String?,
        @SerialName("created_at") val createdAt: String,
        val duration: Short?,
        @SerialName("host_id") val hostId: String?
    )

    @Serializable
    data class PaginationObject(
        @SerialName("next_page_token") val nextPageToken: String,
        @SerialName("page_count") val pageCount: Int,
        @SerialName("page_number") val pageNumber: Int,
        @SerialName("page_size") val pageSize: Int,
        @SerialName("total_records") val totalRecords: Int,
        val meetings: List<ScheduledMeeting>
    )

    @Serializable
    data class Recurrence(
        val type: Short,
        @SerialName("end_date_time") val endDateTime: String? = null,
        @SerialName("end_times") val endTimes: Short? = null,
        @SerialName("monthly_day") val monthlyDay: Short? = null,
        @SerialName("monthly_week") val monthlyWeek: Short? = null,
        @SerialName("monthly_week_day") val monthlyWeekDay: Short? = null,
        @SerialName("repeat_interval") val repeatInterval: Short? = null,
        @SerialName("weekly_days") val weeklyDays: List<Short>? = null
    )
}
