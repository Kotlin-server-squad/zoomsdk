package com.kss.zoom.sdk

import com.kss.zoom.Page
import com.kss.zoom.PagedQuery
import com.kss.zoom.sdk.model.Meeting
import com.kss.zoom.sdk.model.ScheduledMeeting
import com.kss.zoom.utils.call
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MeetingsTest : ZoomTestBase() {

    companion object {
        private const val USER_ID = "DyVG0MGzQJuxaJ4mju20bA"
    }

    private lateinit var meetings: Meetings

    @BeforeEach
    fun setUp() {
        meetings = meetings()
    }

    @AfterEach
    fun tearDown() = runBlocking {
        assertTrue {
            call { meetings.deleteAll(USER_ID) }
        }
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

    @Test
    fun `should get meeting by id`(): Unit = runBlocking {
        val meeting = createMeeting()
        val foundMeeting = call { meetings.get(meeting.id) }
        isEqualIgnoringStartUrl(meeting, foundMeeting)
    }

    @Test
    fun `should delete meeting`(): Unit = runBlocking {
        val meeting = createMeeting()
        val result = call { meetings.delete(meeting.id) }
        assertTrue(result, "Meeting should be deleted")
    }

    @Test
    fun `should list scheduled meetings`(): Unit = runBlocking {
        val userMeetings = createMeetings(3)
        verifyPage(call { meetings.listScheduled(USER_ID) }, userMeetings, expectedPageSize = 3, expectedPageCount = 1)
    }

    @Test
    fun `should list multiple pages of scheduled meetings`(): Unit = runBlocking {
        val userMeetings = createMeetings(10)
        val pageSize = 3
        val pageOne = call { meetings.listScheduled(USER_ID, PagedQuery(pageNumber = 1, pageSize = pageSize)) }
        verifyPage(pageOne, userMeetings, expectedPageSize = pageSize, expectedPageCount = 4)

        val pageTwo = call { meetings.listScheduled(USER_ID, PagedQuery(pageNumber = 2, pageSize = pageSize)) }
        verifyPage(pageTwo, userMeetings, expectedPageSize = pageSize, expectedPageCount = 4)
    }

    private fun verifyPage(
        page: Page<ScheduledMeeting>,
        expectedMeetings: List<Meeting>,
        expectedPageSize: Int,
        expectedPageCount: Int
    ) {
        assertEquals(expectedPageSize, page.items.size, "Page size should match")
        assertEquals(expectedPageCount, page.pageCount, "Page count should match")
        assertEquals(expectedMeetings.size, page.totalRecords, "Total records should match")
        assertTrue(
            page.items.map { it.id }.all { meetingId -> expectedMeetings.any { it.id == meetingId } },
            "All items should be in the expected meetings list"
        )
    }

    private fun verifyMeeting(meeting: Meeting, expectedParams: MeetingParams) {
        assertEquals(expectedParams.topic, meeting.topic, "Topic should match")
        assertEquals(expectedParams.duration, meeting.duration, "Duration should match")
        assertEquals(
            expectedParams.startTime.truncatedTo(ChronoUnit.SECONDS),
            meeting.startTime,
            "Start time should match"
        )
        assertNotNull(meeting.host, "Host should not be null")
        assertEquals(USER_ID, meeting.host.id, "Host id should match")
        assertNotNull(meeting.host.email, "Host email should not be null")
        assertFalse(meeting.uuid.isBlank(), "UUID should not be blank")
        assertTrue(meeting.id > 0, "ID should be greater than 0")
    }

    private suspend fun createMeeting(): Meeting {
        return call {
            meetings.create(
                userId = USER_ID,
                topic = "Test topic",
                startTime = LocalDateTime.now(),
                duration = 60,
                timezone = TimeZone.getTimeZone("Europe/London")
            )
        }
    }

    private suspend fun createMeetings(count: Int): List<Meeting> =
        (1..count).map { createMeeting() }

    private fun isEqualIgnoringStartUrl(expected: Meeting, actual: Meeting) {
        assertFalse(actual.startUrl.isBlank())
        assertEquals(
            expected,
            actual.copy(startUrl = expected.startUrl),
            "Meetings should match"
        )
    }
}

data class MeetingParams(
    val topic: String,
    val duration: Short,
    val timeZone: TimeZone,
    val startTime: LocalDateTime
)