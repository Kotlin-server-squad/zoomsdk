package com.kss.zoom.meetings

data class Meeting(
    val id: String,
    val duration: Long,
    val startedAt: Long,
    val endedAt: Long
)