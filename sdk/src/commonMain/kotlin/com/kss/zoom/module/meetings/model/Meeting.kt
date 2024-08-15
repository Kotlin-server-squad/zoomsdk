package com.kss.zoom.module.meetings.model

data class Meeting(
    val id: String,
    val uuid: String,
    val hostId: String,
    val topic: String,
    val createdAt: Long,
    val startTime: Long,
    val duration: Short,
    val timezone: String,
    val joinUrl: String,
    val startUrl: String,
    val password: String,
)
