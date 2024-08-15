package com.kss.zoom.module.meetings.model.api

import com.kss.zoom.module.meetings.model.Meeting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingResponse(
    val id: Long,
    val uuid: String,
    val duration: Short,
    @SerialName("host_id") val hostId: String,
    @SerialName("host_email") val hostEmail: String,
    val status: String,
    @SerialName("start_time") val startTime: String,
    val timezone: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("start_url") val startUrl: String,
    @SerialName("join_url") val joinUrl: String,
    val password: String,
)

fun MeetingResponse.toModel(): Meeting {
    TODO()
}
