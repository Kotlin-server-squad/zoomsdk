package com.kss.zoomsdk.module.meetings.model

data class Meeting(
    val id: String,
    val uuid: String,
    val hostId: String,
    val topic: String,
    val createdAt: Long,
    val startTime: Long,
    val duration: Short,
    val timeZone: String,
    val joinUrl: String,
    val startUrl: String,
    val password: String,
)
