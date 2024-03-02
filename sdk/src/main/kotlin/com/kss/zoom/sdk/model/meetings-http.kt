package com.kss.zoom.sdk.model

import com.kss.zoom.Page
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import java.util.*


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
data class ScheduledMeetingResponse(
    val id: Long,
    val uuid: String,
    @SerialName("host_id") val hostId: String,
    val agenda: String? = null,
    val topic: String,
    val duration: Short,
    @SerialName("start_time") val startTime: String,
    val timezone: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("join_url") val joinUrl: String
)

@Serializable
data class PaginationObject(
    @SerialName("page_count") val pageCount: Int,
    @SerialName("page_number") val pageNumber: Int,
    @SerialName("page_size") val pageSize: Int,
    @SerialName("total_records") val totalRecords: Int,
    val meetings: List<ScheduledMeetingResponse>,
    @SerialName("next_page_token") val nextPageToken: String? = null
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

fun PaginationObject.toModel(): Page<ScheduledMeeting> =
    Page(
        items = this.meetings.map { it.toModel() },
        pageNumber = pageNumber,
        pageCount = pageCount,
        pageSize = pageSize,
        totalRecords = totalRecords,
        nextPageToken = nextPageToken
    )

fun MeetingResponse.toModel(): Meeting =
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

private fun ScheduledMeetingResponse.toModel(): ScheduledMeeting =
    ScheduledMeeting(
        id = id,
        uuid = uuid,
        hostId = hostId,
        agenda = agenda,
        topic = topic,
        startTime = ZonedDateTime.parse(startTime).toInstant(),
        duration = duration,
        joinUrl = joinUrl,
        createdAt = ZonedDateTime.parse(createdAt).toInstant(),
        timeZone = TimeZone.getTimeZone(timezone)
    )
