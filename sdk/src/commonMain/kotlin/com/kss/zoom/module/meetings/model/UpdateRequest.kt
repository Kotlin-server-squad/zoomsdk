package com.kss.zoom.module.meetings.model

import com.kss.zoom.common.greaterZero
import com.kss.zoom.common.isInFuture
import com.kss.zoom.common.notBlank
import com.kss.zoom.model.request.TimeAwareUserRequest
import com.kss.zoom.module.meetings.model.api.UpdateMeetingRequest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

data class UpdateRequest(
    override val userId: String,
    val meetingId: String,
    val topic: String? = null,
    val startTime: LocalDateTime? = null,
    val duration: Short? = null,
    val timezone: TimeZone? = null,
) : TimeAwareUserRequest {
    init {
        meetingId.notBlank("meetingId")
        topic?.notBlank("topic")
        duration?.greaterZero("duration")

        requireNotNull(topic ?: startTime ?: duration ?: timezone) {
            "At least one of the fields must be set!"
        }
    }

    override fun validate(clock: Clock) {
        startTime?.isInFuture("startTime", clock)
    }
}

fun UpdateRequest.toApi(): UpdateMeetingRequest {
    return UpdateMeetingRequest(
        agenda = topic,
        startTime = startTime?.toString(),
        duration = duration,
        timezone = timezone?.id
    )
}
