package com.kss.zoom.sdk.common.model

import kotlinx.datetime.LocalDateTime

data class DateTimeFilter(val startTime: LocalDateTime, val endTime: LocalDateTime) {
    init {
        require(startTime < endTime) { "Start time must be before end time." }
    }
}