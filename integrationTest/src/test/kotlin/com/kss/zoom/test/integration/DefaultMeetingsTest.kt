package com.kss.zoom.test.integration

import com.kss.zoom.Zoom
import com.kss.zoom.common.call
import com.kss.zoom.common.extensions.id
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.request.PageRequest
import com.kss.zoom.model.request.ListRequest
import com.kss.zoom.module.meetings.Meetings
import com.kss.zoom.module.meetings.model.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import kotlin.test.*
import kotlin.time.Duration.Companion.minutes

class DefaultMeetingsTest : ZoomModuleTest() {

    private lateinit var meetings: Meetings

    override fun setUp(zoom: Zoom) {
        meetings = zoom.meetings()
    }

    @AfterTest
    fun tearDown() = runTest {
        // Assuming it's safe to delete all meetings after each test!
        meetings.deleteAll(DeleteAllRequest(userId = userId))
    }

    @Test
    fun `should create a meeting`() = runTest {
        val request = createRequest()
        val meeting = call { meetings.create(request) }
        verifyMeeting(meeting, request)
    }

    @Test
    fun `should update a meeting`() = runTest {
        val meeting = call { meetings.create(createRequest()) }
        val request = updateRequest(meeting.id, topic = "Updated Test Meeting", duration = 60)
        val updatedMeeting = call { meetings.update(request) }
        verifyMeeting(updatedMeeting, request)
    }

    @Test
    fun `should get a meeting by id`() = runTest {
        val request = createRequest()
        val meeting = call { meetings.create(request) }
        val foundMeeting = call { meetings.get(GetRequest(meeting.id)) }
        assertEquals(meeting.normalize(), foundMeeting.normalize(), "Meetings should be the same")
    }

    @Test
    fun `should delete a meeting`() = runTest {
        val meeting = call { meetings.create(createRequest()) }
        call { meetings.delete(deleteRequest(meeting.id)) }
        when (val result = meetings.get(GetRequest(meeting.id))) {
            is CallResult.Success -> fail("Meeting should not be found")
            is CallResult.Error.NotFound -> {
                // Expected
            }
            else -> fail("Unexpected result: $result")
        }
    }

    @Test
    fun `should list user meetings`() = runTest {
        (1..3).mapIndexed { index, _ ->
            call { meetings.create(createRequest("Test Meeting $index")) }
        }.forEachIndexed { index, meeting ->
            val page = call { meetings.list(ListRequest(userId = userId, pageRequest = PageRequest(index = (index + 1).toShort(), size = 1))) }
            assertEquals(1, page.size, "Page size should be 1")
            assertEquals(1, page.items.size, "Items size should be 1")

            // TODO: Fix this assertion - it's failing because the returned meeting lacks the startUrl, hostEmail and password fields
            assertEquals(meeting.id, page.items.first().id, "Meetings should be the same")
        }
    }

    private fun Meeting.normalize(): Meeting {
        return copy(
            startUrl = this.startUrl?.let {
                val url = Url(it)
                "${url.protocol}://${url.host}${url.encodedPath}"
            }
        )
    }

    private fun createRequest(
        topic: String = "Test Meeting",
        duration: Short = 30,
        timezone: TimeZone = TimeZone.UTC,
    ): CreateRequest {
        return CreateRequest(
            userId = userId,
            topic = topic,
            startTime = Clock.System.now().plus(10.minutes).toLocalDateTime(timezone),
            duration = duration,
            timezone = timezone
        )
    }

    private fun updateRequest(
        meetingId: String,
        topic: String,
        startTime: LocalDateTime = tenMinutesFromNow(),
        duration: Short = 30,
        timezone: TimeZone = TimeZone.UTC,
    ): UpdateRequest {
        return UpdateRequest(
            userId = userId,
            meetingId = meetingId,
            topic = topic,
            startTime = startTime,
            duration = duration,
            timezone = timezone
        )
    }

    private fun GetRequest(meetingId: String): GetRequest {
        return GetRequest(
            userId = userId,
            meetingId = meetingId
        )
    }

    private fun deleteRequest(meetingId: String): DeleteRequest {
        return DeleteRequest(
            userId = userId,
            meetingId = meetingId
        )
    }

    private fun verifyMeeting(meeting: Meeting, request: CreateRequest) {
        assertEquals(request.topic, meeting.topic, "Meeting topic should be the same")
        assertTrue(
            request.startTime.toInstant(request.timezone).toEpochMilliseconds() - meeting.startTime < 1000,
            "Meeting start time should be the same with 1 second tolerance"
        )
        assertEquals(request.duration, meeting.duration, "Meeting duration should be the same")
        assertEquals(request.timezone.id(), meeting.timezone, "Meeting timezone should be the same")
    }

    private fun verifyMeeting(meeting: Meeting, request: UpdateRequest) {
        assertEquals(request.topic, meeting.topic, "Meeting topic should be the same")
        request.startTime?.let { startTime ->
            assertTrue(
                startTime.toInstant(request.timezone ?: TimeZone.UTC).toEpochMilliseconds() - meeting.startTime < 1000,
                "Meeting start time should be the same with 1 second tolerance"
            )
        }
        assertEquals(request.duration, meeting.duration, "Meeting duration should be the same")
        assertEquals(request.timezone?.id(), meeting.timezone, "Meeting timezone should be the same")
    }

    private fun tenMinutesFromNow(): LocalDateTime {
        return Clock.System.now().plus(10.minutes).toLocalDateTime(TimeZone.UTC)
    }
}
