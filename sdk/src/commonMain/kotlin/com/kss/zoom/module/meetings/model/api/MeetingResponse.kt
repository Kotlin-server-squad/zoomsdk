package com.kss.zoom.module.meetings.model.api

import com.kss.zoom.common.extensions.toTimestamp
import com.kss.zoom.module.meetings.model.Meeting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingResponse(
    val id: Long,
    val uuid: String,
    val topic: String,
    val duration: Short,
    @SerialName("host_id") val hostId: String,
    @SerialName("host_email") val hostEmail: String? = null,
    val status: String? = null,
    @SerialName("start_time") val startTime: String,
    val timezone: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("start_url") val startUrl: String? = null,
    @SerialName("join_url") val joinUrl: String,
    val password: String? = null,
)

fun MeetingResponse.toModel(): Meeting {
    return Meeting(
        id = id.toString(),
        uuid = uuid,
        topic = topic,
        duration = duration,
        hostId = hostId,
        createdAt = createdAt.toTimestamp(),
        startTime = startTime.toTimestamp(),
        timezone = timezone,
        joinUrl = joinUrl,
        status = status,
        hostEmail = hostEmail,
        startUrl = startUrl,
        password = password
    )
}
