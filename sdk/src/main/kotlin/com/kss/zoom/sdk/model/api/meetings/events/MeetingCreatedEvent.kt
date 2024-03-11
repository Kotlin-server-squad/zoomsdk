package com.kss.zoom.sdk.model.api.meetings.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingCreatedEvent(
    val event: String,
    @SerialName("event_ts") val timestamp: Long,
    val payload: Payload
) {
    @Serializable
    data class Payload(
        @SerialName("account_id") val accountId: String,
        val operator: String,
        @SerialName("operator_id") val operatorId: String,
        @SerialName("object") val data: Data
    ) {
        @Serializable
        data class Data(
            val id: Long,
            val uuid: String,
            @SerialName("host_id") val hostId: String,
            val topic: String,
            val type: Int,
            @SerialName("start_time") val startTime: String? = null,
            val timezone: String? = null,
            val duration: Short,
            @SerialName("join_url") val joinUrl: String,
            val password: String? = null
        )
    }
}
