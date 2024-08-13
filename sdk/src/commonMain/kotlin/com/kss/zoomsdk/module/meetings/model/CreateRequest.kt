package com.kss.zoomsdk.module.meetings.model

data class CreateRequest(
    val userId: String,
    val topic: String,
    val startTime: Long,
    val duration: Short,
    val timezone: String
)
