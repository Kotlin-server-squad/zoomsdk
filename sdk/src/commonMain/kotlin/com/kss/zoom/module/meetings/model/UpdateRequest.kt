package com.kss.zoom.module.meetings.model

import com.kss.zoom.common.extensions.toIsoDateTimeString
import com.kss.zoom.common.greaterZero
import com.kss.zoom.common.notBlank
import com.kss.zoom.module.meetings.model.api.UpdateMeetingRequest
import kotlinx.datetime.TimeZone

data class UpdateRequest(
    val userId: String,
    val meetingId: String,
    val topic: String? = null,
    val startTime: Long? = null,
    val duration: Short? = null,
    val timezone: String? = null,
) {
    init {
        meetingId.notBlank("meetingId")
        topic?.notBlank("topic")
        startTime?.greaterZero("startTime")
        timezone?.notBlank("timezone")
        duration?.greaterZero("duration")

        requireNotNull(topic ?: startTime ?: duration ?: timezone) {
            "At least one of the fields must be set!"
        }
    }
}

fun UpdateRequest.toApi(): UpdateMeetingRequest {
    return UpdateMeetingRequest(
        agenda = topic,
        startTime = startTime?.toIsoDateTimeString(timezone ?: TimeZone.UTC.id),
        duration = duration,
        timezone = timezone
    )
}
