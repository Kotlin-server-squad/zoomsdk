package com.kss.zoom.sdk.meetings.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOneTimeMeetingRequest(
    val topic: String,
    val type: Short,
    @SerialName("start_time") val startTime: String? = null,
    val duration: Short? = null,
    val timezone: String? = null
)