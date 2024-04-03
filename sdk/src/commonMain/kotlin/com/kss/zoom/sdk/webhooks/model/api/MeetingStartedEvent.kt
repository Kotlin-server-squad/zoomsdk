package com.kss.zoom.sdk.webhooks.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingStartedEvent(
    val event: String,
    @SerialName("event_ts") val timestamp: Long,
    val payload: Payload
) {
    @Serializable
    data class Payload(
        @SerialName("account_id") val accountId: String,
        @SerialName("object") val data: Data
    ) {
        @Serializable
        data class Data(
            val id: String,
            val uuid: String,
            @SerialName("host_id") val hostId: String,
            val topic: String,
            val type: Int,
            @SerialName("start_time") val startTime: String,
            val timezone: String? = null,
            val duration: Short
        )
    }
}

