package com.kss.zoom.sdk.meetings.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

data class ScheduledMeeting(
    val id: Long,
    val uuid: String,
    val hostId: String,
    val agenda: String?,
    val topic: String?,
    val startTime: LocalDateTime,
    val duration: Short,
    val createdAt: Instant,
    val timeZone: TimeZone?,
    val joinUrl: String
)