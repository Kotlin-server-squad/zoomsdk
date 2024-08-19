package com.kss.zoom.module.meetings.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingRequest(
    val topic: String,
    val type: Short,
    @SerialName("start_time") val startTime: String,
    val duration: Short,
    val timezone: String,
)
