package com.kss.zoom.sdk.meetings.model

import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class Meeting(
    val id: Long,
    val uuid: String,
    val topic: String,
    val host: MeetingHost,
    val startTime: LocalDateTime,
    val duration: Short,
    val joinUrl: String,
    val startUrl: String,
    val password: String,
    val createdAt: Instant,
    val timeZone: TimeZone? = null,
    val schedule: Schedule? = null,
)