package com.kss.zoom.sdk.meetings.model.api

import com.kss.zoom.sdk.meetings.model.ScheduledMeeting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import java.util.*

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

fun ScheduledMeetingResponse.toDomain(): ScheduledMeeting =
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