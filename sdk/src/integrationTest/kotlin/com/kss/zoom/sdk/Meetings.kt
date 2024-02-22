package com.kss.zoom.sdk

import com.kss.zoom.sdk.meetings.Meeting
import com.kss.zoom.sdk.meetings.Meetings
import com.kss.zoom.utils.call
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class Meetings : ZoomTestBase() {

    companion object {
        const val USER_ID = "lqkrEKqMR1CCmALIVs73RQ"
    }

    private lateinit var meetings: Meetings

    @BeforeEach
    fun setUp() {
        meetings = meetings()
    }

    @Test
    fun `should create meeting with expected parameters`(): Unit = runBlocking {
        val params = MeetingParams(
            topic = "Test topic",
            duration = 60,
            timeZone = TimeZone.getTimeZone("Europe/London"),
            startTime = LocalDateTime.now()
        )
        val meeting = call {
            meetings.create(
                userId = USER_ID,
                topic = params.topic,
                startTime = params.startTime,
                duration = params.duration,
                timezone = params.timeZone
            )
        }
        verifyMeeting(meeting, params)
    }

    private fun verifyMeeting(meeting: Meeting, expectedParams: MeetingParams) {
        assertEquals(expectedParams.topic, meeting.topic, "Topic should match")
        assertEquals(expectedParams.duration, meeting.duration, "Duration should match")
        assertEquals(expectedParams.startTime.truncatedTo(ChronoUnit.SECONDS), meeting.startTime, "Start time should match")
        assertNotNull(meeting.host, "Host should not be null")
        assertEquals(USER_ID, meeting.host.id, "Host id should match")
        assertNotNull(meeting.host.email, "Host email should not be null")
        assertFalse(meeting.uuid.isBlank(), "UUID should not be blank")
        assertTrue(meeting.id > 0, "ID should be greater than 0")
    }
}
data class MeetingParams(
    val topic: String,
    val duration: Short,
    val timeZone: TimeZone,
    val startTime: LocalDateTime
)