package com.kss.zoom.module.meetings.model

import com.kss.zoom.common.greaterZero
import com.kss.zoom.common.notBlank

data class CreateRequest(
    val userId: String,
    val topic: String,
    val startTime: Long,
    val duration: Short,
    val timezone: String
) {
    init {
        userId.notBlank("userId")
        topic.notBlank("topic")
        timezone.notBlank("timezone")
        duration.greaterZero("duration")
    }
}
