package com.kss.zoom.sdk.model.domain.meetings

import java.time.Instant
import java.util.*

data class ScheduledMeeting(
    val id: Long,
    val uuid: String,
    val hostId: String,
    val agenda: String?,
    val topic: String?,
    val startTime: Instant,
    val duration: Short,
    val createdAt: Instant,
    val timeZone: TimeZone?,
    val joinUrl: String
)