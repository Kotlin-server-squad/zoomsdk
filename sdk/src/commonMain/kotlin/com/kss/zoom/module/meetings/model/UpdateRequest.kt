package com.kss.zoom.module.meetings.model

import com.kss.zoom.common.greaterZero
import com.kss.zoom.common.notBlank

data class UpdateRequest(
    val meetingId: String,
    val topic: String? = null,
    val startTime: Long? = null,
    val duration: Short? = null,
    val timezone: String? = null
) {
    init {
        meetingId.notBlank("meetingId")
        topic?.notBlank("topic")
        startTime?.greaterZero("startTime")
        timezone?.notBlank("timezone")
        duration?.greaterZero("duration")
    }
}
