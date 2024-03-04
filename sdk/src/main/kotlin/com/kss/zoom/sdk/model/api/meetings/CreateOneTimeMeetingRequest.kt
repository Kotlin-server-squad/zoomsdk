package com.kss.zoom.sdk.model.api.meetings

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