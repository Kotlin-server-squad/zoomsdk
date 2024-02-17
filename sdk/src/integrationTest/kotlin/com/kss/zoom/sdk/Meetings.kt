package com.kss.zoom.sdk

import com.kss.zoom.sdk.meetings.Meetings
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Meetings : ZoomTestBase() {

    private lateinit var meetings: Meetings

    @BeforeEach
    fun setUp() {
        meetings = meetings()
    }

    @Test
    fun `should load`() {

    }
}