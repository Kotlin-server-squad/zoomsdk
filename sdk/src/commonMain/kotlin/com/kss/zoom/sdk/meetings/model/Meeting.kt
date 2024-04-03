package com.kss.zoom.sdk.meetings.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

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