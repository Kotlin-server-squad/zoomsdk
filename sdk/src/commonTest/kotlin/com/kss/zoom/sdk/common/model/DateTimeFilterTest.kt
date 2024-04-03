package com.kss.zoom.sdk.common.model

import com.kss.zoom.assertThrows
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeFilterTest {

    @Test
    fun startTimeShouldBeBeforeEndTime() {
        val exception = assertThrows(IllegalArgumentException::class) {
            DateTimeFilter(
                startTime = LocalDateTime(2024, 3, 1, 0, 1),
                endTime = LocalDateTime(2024, 3, 1, 0, 0)
            )
        }
        assertEquals("Start time must be before end time.", exception.message)
    }
}