package com.kss.zoom.sdk.meetings.model.api

import com.kss.zoom.sdk.common.toInstant
import com.kss.zoom.sdk.common.toTimeZone
import com.kss.zoom.sdk.common.zonedToLocalDateTime
import com.kss.zoom.sdk.meetings.model.ScheduledMeeting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        startTime = startTime.zonedToLocalDateTime(),
        duration = duration,
        joinUrl = joinUrl,
        createdAt = createdAt.toInstant(),
        timeZone = timezone.toTimeZone()
    )
