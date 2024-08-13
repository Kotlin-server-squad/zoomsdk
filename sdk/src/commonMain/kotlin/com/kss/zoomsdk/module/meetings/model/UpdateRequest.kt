package com.kss.zoomsdk.module.meetings.model

data class UpdateRequest(
    val meetingId: String,
    val topic: String? = null,
    val startTime: Long? = null,
    val duration: Short? = null,
    val timezone: String? = null
)
