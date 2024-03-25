package com.kss.zoom.sdk.meetings.model.api

import com.kss.zoom.sdk.meetings.model.Meeting
import com.kss.zoom.sdk.meetings.model.MeetingHost
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import java.util.*

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

fun MeetingResponse.toDomain(): Meeting =
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