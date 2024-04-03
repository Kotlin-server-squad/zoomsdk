package com.kss.zoom.sdk.webhooks.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingParticipantLeftEvent(
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
            val duration: Short,
            val participant: Participant
        ) {
            @Serializable
            data class Participant(
                val id: String? = null,
                @SerialName("user_id") val userId: String,
                @SerialName("user_name") val username: String? = null,
                @SerialName("participant_uuid") val participantUuid: String? = null,
                @SerialName("leave_time") val leaveTime: String,
                @SerialName("leave_reason") val leaveReason: String? = null,
                val email: String,
                @SerialName("registrant_id") val registrantId: String? = null,
                @SerialName("participant_user_id") val participantUserId: String? = null,
                @SerialName("customer_key") val customerKey: String? = null,
                @SerialName("phone_number") val phoneNumber: String? = null
            )
        }
    }
}
