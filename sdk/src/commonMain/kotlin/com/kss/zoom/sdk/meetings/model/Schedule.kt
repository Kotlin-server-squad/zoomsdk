package com.kss.zoom.sdk.meetings.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

data class Schedule(
    val recurrence: Recurrence,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val endTimes: Int? = null,
    val timeZone: TimeZone? = null,
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