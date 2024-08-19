package com.kss.zoom.module.meetings

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.meetings.model.CreateRequest
import com.kss.zoom.module.meetings.model.DeleteRequest
import com.kss.zoom.module.meetings.model.GetRequest
import com.kss.zoom.module.meetings.model.UpdateRequest
import com.kss.zoom.module.meetings.model.api.MeetingResponse
import com.kss.zoom.module.meetings.model.api.toModel
import com.kss.zoom.test.testClock
import com.kss.zoom.test.withMockClient
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.time.Duration.Companion.minutes

class DefaultMeetingsTest {
    companion object {
        private const val MEETING_ID = "userId"
        private const val USER_ID = "userId"
        private const val ACCESS_TOKEN = "accessToken"
        private val meetingResponse = MeetingResponse(
            id = 1,
            uuid = "uuid",
            topic = "topic",
            duration = 60,
            hostId = "hostId",
            hostEmail = "hostEmail",
            status = "status",
            startTime = "2024-01-10T00:00:00Z",
            createdAt = "2024-01-01T10:00:00Z",
            timezone = "timezone",
            startUrl = "startUrl",
            joinUrl = "joinUrl",
            password = "password"
        )
    }

    @Test
    fun `should create a meeting`() = runTest {
        withMockClient(
            mockClient = mock {
                everySuspend {
                    post<MeetingResponse>(
                        path = "/meetings",
                        token = ACCESS_TOKEN,
                        contentType = "application/json",
                        body = any()
                    )
                } calls { _ ->
                    CallResult.Success(meetingResponse)
                }
            }
        ) {
            when (val result = meetings(it).create(
                CreateRequest(
                    userId = USER_ID,
                    topic = "topic",
                    startTime = testClock.plus(1.minutes),
                    duration = 60,
                    timezone = TimeZone.UTC
                )
            )) {
                is CallResult.Success -> {
                    assertEquals(meetingResponse.toModel(), result.data, "Meeting should be equal")
                }

                else -> fail("Unexpected result: $result")
            }
        }
    }

    @Test
    fun `should update a meeting`() = runTest {
        val updateRequest = UpdateRequest(
            userId = USER_ID,
            meetingId = MEETING_ID,
            topic = "updatedTopic",
            startTime = testClock.plus(1.minutes),
            duration = 1200,
            timezone = TimeZone.UTC
        )
        val expectedMeetingResponse = meetingResponse.copy(
            topic = updateRequest.topic!!,
            startTime = updateRequest.startTime?.let { testClock.toIsoString(it) }!!,
            duration = updateRequest.duration!!,
            timezone = updateRequest.timezone?.id!!
        )
        withMockClient(
            mockClient = mock {
                everySuspend {
                    patch<Unit>(
                        path = "/meetings/$MEETING_ID",
                        token = ACCESS_TOKEN,
                        contentType = "application/json",
                        body = any()
                    )
                } returns CallResult.Success(Unit)
                everySuspend {
                    get<MeetingResponse>("meetings/$MEETING_ID", ACCESS_TOKEN)
                } returns CallResult.Success(expectedMeetingResponse)
            }
        ) {
            meetings(it).update(updateRequest).let { result ->
                when (result) {
                    is CallResult.Success -> {
                        assertEquals(expectedMeetingResponse.toModel(), result.data, "Meeting should be equal")
                    }

                    else -> fail("Unexpected result: $result")
                }
            }
        }
    }

    @Test
    fun `should get meeting details`() = runTest {
        withMockClient(
            mockClient = mock {
                everySuspend {
                    get<MeetingResponse>("meetings/1", ACCESS_TOKEN)
                } returns CallResult.Success(meetingResponse)
            }
        ) {
            meetings(it).get(GetRequest(userId = USER_ID, meetingId = "1")).let { result ->
                when (result) {
                    is CallResult.Success -> {
                        assertEquals(meetingResponse.toModel(), result.data, "Meeting should be equal")
                    }

                    else -> fail("Unexpected result: $result")
                }
            }
        }
    }

    @Test
    fun `should delete meeting`() = runTest {
        withMockClient(
            mockClient = mock {
                everySuspend {
                    delete<MeetingResponse>("meetings/1", ACCESS_TOKEN)
                } returns CallResult.Success(meetingResponse)
            }
        ) {
            meetings(it).delete(DeleteRequest(userId = USER_ID, meetingId = "1")).let { result ->
                when (result) {
                    is CallResult.Success -> {
                        assertEquals(meetingResponse.toModel(), result.data, "Meeting should be equal")
                    }

                    else -> fail("Unexpected result: $result")
                }
            }
        }
    }

    // TODO verify CallResult.Error
//    @Test
    fun `should not accept an invalid request to create a meeting`() = runTest {
        withMockClient {
            meetings(it).create(
                CreateRequest(
                    userId = USER_ID,
                    topic = "topic",
                    startTime = testClock.minus(1.minutes), // Invalid start date!
                    duration = 60,
                    timezone = TimeZone.UTC
                )
            )
        }
    }

    private fun meetings(
        client: ApiClient,
        auth: Auth = mock {},
        storage: TokenStorage = mock {
            everySuspend { getAccessToken(USER_ID) } returns ACCESS_TOKEN
        },
    ): DefaultMeetings {
        return DefaultMeetings(auth, storage, testClock, client)
    }
}
