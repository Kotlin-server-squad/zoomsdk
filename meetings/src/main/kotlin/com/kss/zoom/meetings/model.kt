package com.kss.zoom.meetings

data class Meeting(
    val id: Long,
    val joinUrl: String,
    val passcode: String,
    val agenda: String
)

data class ScheduledMeeting(
    val id: Long,
    val duration: Long,
    val joinUrl: String,
    val passcode: String,
    val agenda: String
)

class ApiException(cause: Throwable) : RuntimeException(cause)