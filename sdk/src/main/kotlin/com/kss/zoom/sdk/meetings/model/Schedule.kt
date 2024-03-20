package com.kss.zoom.sdk.meetings.model

import java.time.ZonedDateTime

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