package com.kss.zoom.module.meetings.model

import com.kss.zoom.common.extensions.id
import com.kss.zoom.common.extensions.toDateTimeString
import com.kss.zoom.common.greaterZero
import com.kss.zoom.common.isInFuture
import com.kss.zoom.common.notBlank
import com.kss.zoom.model.request.TimeAwareUserRequest
import com.kss.zoom.module.meetings.model.api.MeetingRequest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

data class CreateRequest(
    override val userId: String,
    val topic: String,
    val startTime: LocalDateTime,
    val duration: Short,
    val timezone: TimeZone,
) : TimeAwareUserRequest {
    init {
        userId.notBlank("userId")
        topic.notBlank("topic")
        duration.greaterZero("duration")
    }

    override fun validate(clock: Clock) {
        startTime.isInFuture("startTime", clock)
    }
}

fun CreateRequest.toApi(): MeetingRequest =
    MeetingRequest(
        topic = topic,
        type = 2,
        startTime = startTime.toDateTimeString(),
        duration = duration,
        timezone = timezone.id()
    )
