package com.kss.zoom.module.meetings.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateMeetingRequest(
    val agenda: String? = null,
    @SerialName("start_time") val startTime: String? = null,
    val duration: Short? = null,
    val timezone: String? = null,
)
