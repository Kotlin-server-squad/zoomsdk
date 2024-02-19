package com.kss.zoom.sdk.meetings

import com.kss.zoom.sdk.users.UserId
import java.time.ZonedDateTime

data class Meeting(
    val id: Long,
    val agenda: String,
    val duration: Long,
    val schedule: Schedule,
    val joinUrl: String,
    val passcode: String
)

data class CreateMeetingRequest(
    val userId: UserId,
    val agenda: String,
    val schedule: Schedule,
    val passcode: String? = null
)

data class Schedule(
    val recurrence: Recurrence,
    val startTime: ZonedDateTime,
    val duration: Long,
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