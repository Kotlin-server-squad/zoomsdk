package com.kss.zoom.sdk.meetings

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime
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

data class MeetingHost(
    val id: String,
    val email: String
)

data class Schedule(
    val recurrence: Recurrence,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime? = null,
    val endTimes: Int? = null,
) {
    enum class Week {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
        LAST
    }

    enum class WeekDay {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY
    }

    sealed interface Recurrence {
        data object Daily : Recurrence
        data class Weekly(val day: WeekDay) : Recurrence
        data class MonthlyByWeek(val week: Week) : Recurrence
        data class MonthlyByWeekDay(val day: WeekDay) : Recurrence
        data class MonthlyByDay(val day: Int) : Recurrence
    }
}