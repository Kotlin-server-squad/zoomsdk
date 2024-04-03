package com.kss.zoom.sdk.meetings.model.api

import com.kss.zoom.sdk.common.*
import com.kss.zoom.sdk.meetings.model.Meeting
import com.kss.zoom.sdk.meetings.model.MeetingHost
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

fun MeetingResponse.toDomain(): Meeting {
    return Meeting(
        id = id,
        uuid = uuid,
        topic = topic,
        host = MeetingHost(
            id = hostId,
            email = hostEmail
        ),
        startTime = startTime.zonedToLocalDateTime(),
        duration = duration,
        joinUrl = joinUrl,
        startUrl = startUrl,
        password = password,
        createdAt = createdAt.toInstant(),
        timeZone = this.timezone.toTimeZone(),
    )
}
