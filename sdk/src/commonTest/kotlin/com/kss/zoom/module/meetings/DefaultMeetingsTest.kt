package com.kss.zoom.module.meetings

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.ZoomModuleConfig
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.meetings.model.*
import com.kss.zoom.module.meetings.model.api.MeetingResponse
import com.kss.zoom.module.meetings.model.api.toModel
import com.kss.zoom.test.*
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
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
        val createRequest = CreateRequest(
            userId = USER_ID,
            topic = "topic",
            startTime = testClock.plus(1.minutes),
            duration = 60,
            timezone = TimeZone.UTC
        )
        withMockClient(
            MockEngine { request ->
                request.assertMethod(HttpMethod.Post)
                request.assertUrl("https://api.zoom.us/v2/users/${USER_ID}/meetings")
                request.assertBearerAuth(ACCESS_TOKEN)
                request.assertContentType(ContentType.Application.Json)
                request.assertBodyAsJson(createRequest.toApi().toJson())
                respondJson(meetingResponse.toJson())
            }
        ) {
            when (val result = meetings(it).create(createRequest)) {
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
            MockEngine { request ->
                when (request.method) {
                    HttpMethod.Patch -> {
                        request.assertUrl("https://api.zoom.us/v2/meetings/$MEETING_ID")
                        request.assertBearerAuth(ACCESS_TOKEN)
                        request.assertContentType(ContentType.Application.Json)
                        request.assertBodyAsJson(updateRequest.toApi().toJson())
                        respondOk()
                    }

                    HttpMethod.Get -> {
                        request.assertUrl("https://api.zoom.us/v2/meetings/$MEETING_ID")
                        request.assertBearerAuth(ACCESS_TOKEN)

                        respond(
                            content = ByteReadChannel(expectedMeetingResponse.toJson()),
                            status = HttpStatusCode.OK,
                            headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                        )
                    }

                    else -> fail("Unexpected request: $request")
                }
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
            MockEngine { request ->
                request.assertMethod(HttpMethod.Get)
                request.assertUrl("https://api.zoom.us/v2/meetings/1")
                request.assertBearerAuth(ACCESS_TOKEN)
                respondJson(meetingResponse.toJson())
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
            MockEngine { request ->
                request.assertMethod(HttpMethod.Delete)
                request.assertBearerAuth(ACCESS_TOKEN)
                respondOk()
            }
        ) {
            meetings(it).delete(DeleteRequest(userId = USER_ID, meetingId = "1")).let { result ->
                when (result) {
                    is CallResult.Success -> {
                        // No data to assert
                    }

                    else -> fail("Unexpected result: $result")
                }
            }
        }
    }

    @Test
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
        return DefaultMeetings(ZoomModuleConfig(), auth, storage, testClock, client)
    }
}
