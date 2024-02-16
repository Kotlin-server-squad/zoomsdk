package com.kss.zoom.sdk

import com.kss.zoom.sdk.meetings.Meetings
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Meetings {

    private lateinit var meetings: Meetings

    @BeforeEach
    fun setUp() = runBlocking {
        meetings = ZoomTestBase.meetings()
    }

    @Test
    fun `should load`() {

    }
}