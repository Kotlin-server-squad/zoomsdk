package com.kss.zoom.module.meetings

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.meetings.model.CreateRequest
import com.kss.zoom.module.meetings.model.DeleteRequest
import com.kss.zoom.module.meetings.model.GetRequest
import com.kss.zoom.module.meetings.model.api.MeetingResponse
import com.kss.zoom.module.meetings.model.api.toModel
import com.kss.zoom.test.withMockClient
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

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
            startTime = "2024-01-01T00:00:00Z",
            createdAt = "2024-01-01T00:00:00Z",
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
                    startTime = 0,
                    duration = 60,
                    timezone = "timezone"
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
        withMockClient(
            mockClient = mock {
                everySuspend {
                    patch<Unit>(
                        path = "/meetings/$MEETING_ID",
                        token = ACCESS_TOKEN
                    )
                } returns CallResult.Success(Unit)
                everySuspend {
                    get<MeetingResponse>("meetings/$MEETING_ID", ACCESS_TOKEN)
                } returns CallResult.Success(meetingResponse)
            }
        ) {

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

    private fun meetings(
        client: ApiClient,
        auth: Auth = mock {},
        storage: TokenStorage = mock {
            everySuspend { getAccessToken(USER_ID) } returns ACCESS_TOKEN
        },
    ): DefaultMeetings {
        return DefaultMeetings(auth, storage, client)
    }
}
