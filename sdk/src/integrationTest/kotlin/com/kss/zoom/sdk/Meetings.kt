package com.kss.zoom.sdk

import com.kss.zoom.sdk.meetings.Meetings
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Meetings {

    private lateinit var meetings: Meetings

    @BeforeEach
    fun setUp() {
        meetings = ZoomTestBase.meetings()
    }

    @Test
    fun `should load`() {

    }
}