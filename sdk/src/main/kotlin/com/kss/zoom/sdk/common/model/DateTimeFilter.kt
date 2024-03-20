package com.kss.zoom.sdk.common.model

import java.time.LocalDateTime

data class DateTimeFilter(val startDate: LocalDateTime, val endDate: LocalDateTime) {
    init {
        require(startDate.isBefore(endDate)) { "Start date must be before end date." }
    }
}
