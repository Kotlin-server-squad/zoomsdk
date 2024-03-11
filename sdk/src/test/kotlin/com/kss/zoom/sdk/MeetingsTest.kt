package com.kss.zoom.sdk

import com.kss.zoom.sdk.WebhookVerifier.Companion.DEFAULT_SIGNATURE_HEADER_NAME
import com.kss.zoom.sdk.WebhookVerifier.Companion.DEFAULT_TIMESTAMP_HEADER_NAME
import com.kss.zoom.sdk.model.api.PaginationObject
import com.kss.zoom.sdk.model.api.meetings.MeetingResponse
import com.kss.zoom.sdk.model.api.meetings.toDomain
import com.kss.zoom.sdk.model.api.toDomain
import com.kss.zoom.sdk.utils.*
import com.kss.zoom.sdk.utils.WebhookTestUtils.TIMESTAMP
import com.kss.zoom.sdk.utils.WebhookTestUtils.validSignature
import com.kss.zoom.utils.call
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class MeetingsTest : ModuleTest<Meetings>() {
    override fun module(): Meetings = meetings()
    override suspend fun sdkCall(module: Meetings): Any =
        module.listScheduled(USER_ID)

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val defaultMeetings = meetings()

    @Test
    fun `should create meeting`() = runBlocking {
        val jsonResponse = """
            {
              "uuid": "dzFrpLGqRcOtpiQQeAVDVA==",
              "id": 78723497365,
              "host_id": "lqkrEKqMR1CCmALIVs73RQ",
              "host_email": "zezulatomas@gmail.com",
              "topic": "Your Meeting Title",
              "type": 2,
              "status": "waiting",
              "start_time": "2024-02-21T08:49:58Z",
              "duration": 60,
              "timezone": "America/Los_Angeles",
              "created_at": "2024-02-21T08:49:58Z",
              "start_url": "https://us04web.zoom.us/s/78723497365?zak=eyJ0eXAiOiJKV1QiLCJzdiI6IjAwMDAwMSIsInptX3NrbSI6InptX28ybSIsImFsZyI6IkhTMjU2In0.eyJhdWQiOiJjbGllbnRzbSIsInVpZCI6Imxxa3JFS3FNUjFDQ21BTElWczczUlEiLCJpc3MiOiJ3ZWIiLCJzayI6IjAiLCJzdHkiOjEsIndjZCI6InVzMDQiLCJjbHQiOjAsIm1udW0iOiI3ODcyMzQ5NzM2NSIsImV4cCI6MTcwODUxMjU5OCwiaWF0IjoxNzA4NTA1Mzk4LCJhaWQiOiJQdDZ2RU4zTFM2eTlyQWhEWHhTNnNnIiwiY2lkIjoiIn0.J6PzfK6SCJNnERbgv_GTvyId9ZjNCRva_FPLUOLVQio",
              "join_url": "https://us04web.zoom.us/j/78723497365?pwd=67MuOC4RaEDM5wbu1NajMVnvadebNY.1",
              "password": "6xTgtV",
              "h323_password": "068699",
              "pstn_password": "068699",
              "encrypted_password": "67MuOC4RaEDM5wbu1NajMVnvadebNY.1"
            }
        """.trimIndent()
        val meeting = call {
            meetings(jsonResponse).create(
                userId = USER_ID,
                topic = "My Meeting",
                startTime = LocalDateTime.now(CONSTANT_CLOCK),
                duration = 60,
                timezone = CLIENT_TIMEZONE
            )
        }
        val expectedMeeting = parseJson<MeetingResponse>(jsonResponse).toDomain()
        assertEquals(expectedMeeting, meeting, "Unexpected meeting")
    }

    @Test
    fun `should get meeting`() = runBlocking {
        val jsonResponse = """
            {
              "uuid": "dzFrpLGqRcOtpiQQeAVDVA==",
              "id": 78723497365,
              "host_id": "lqkrEKqMR1CCmALIVs73RQ",
              "host_email": "zezulatomas@gmail.com",
              "topic": "Your Meeting Title",
              "type": 2,
              "status": "waiting",
              "start_time": "2024-02-21T08:49:58Z",
              "duration": 60,
              "timezone": "America/Los_Angeles",
              "created_at": "2024-02-21T08:49:58Z",
              "start_url": "https://us04web.zoom.us/s/78723497365?zak=eyJ0eXAiOiJKV1QiLCJzdiI6IjAwMDAwMSIsInptX3NrbSI6InptX28ybSIsImFsZyI6IkhTMjU2In0.eyJhdWQiOiJjbGllbnRzbSIsInVpZCI6Imxxa3JFS3FNUjFDQ21BTElWczczUlEiLCJpc3MiOiJ3ZWIiLCJzayI6IjAiLCJzdHkiOjEsIndjZCI6InVzMDQiLCJjbHQiOjAsIm1udW0iOiI3ODcyMzQ5NzM2NSIsImV4cCI6MTcwODUxMjU5OCwiaWF0IjoxNzA4NTA1Mzk4LCJhaWQiOiJQdDZ2RU4zTFM2eTlyQWhEWHhTNnNnIiwiY2lkIjoiIn0.J6PzfK6SCJNnERbgv_GTvyId9ZjNCRva_FPLUOLVQio",
              "join_url": "https://us04web.zoom.us/j/78723497365?pwd=67MuOC4RaEDM5wbu1NajMVnvadebNY.1",
              "password": "6xTgtV",
              "h323_password": "068699",
              "pstn_password": "068699",
              "encrypted_password": "67MuOC4RaEDM5wbu1NajMVnvadebNY.1"
            }
        """.trimIndent()
        val meeting = call { meetings(jsonResponse).get(78723497365) }
        val expectedMeeting = parseJson<MeetingResponse>(jsonResponse).toDomain()
        assertEquals(expectedMeeting, meeting, "Unexpected meeting")
    }

    @Test
    fun `should delete meeting`() = runBlocking {
        assertTrue(call { meetings().delete(78723497365) }, "Meeting not deleted")
    }

    @Test
    fun `should delete all user meetings`() = runBlocking {
        assertTrue(call { meetings().deleteAll(USER_ID) }, "User meetings not deleted")
    }

    @Test
    fun `should list scheduled meetings`() = runBlocking {
        val jsonResponse = """
            {
              "page_count": 2,
              "page_number": 1,
              "page_size": 30,
              "total_records": 56,
              "meetings": [
                {
                  "uuid": "7gH0gmRgSoeoo/1h0pHzAw==",
                  "id": 76920264316,
                  "host_id": "lqkrEKqMR1CCmALIVs73RQ",
                  "topic": "Zoom Meeting",
                  "type": 2,
                  "start_time": "2024-02-21T08:47:59Z",
                  "duration": 60,
                  "timezone": "Europe/Prague",
                  "agenda": "Test agenda",
                  "created_at": "2024-02-21T08:47:59Z",
                  "join_url": "https://us04web.zoom.us/j/76920264316?pwd=cUkK6ywWlauFusJori4chv9TZy5Ma5.1"
                },
                {
                  "uuid": "dzFrpLGqRcOtpiQQeAVDVA==",
                  "id": 78723497365,
                  "host_id": "lqkrEKqMR1CCmALIVs73RQ",
                  "topic": "Your Meeting Title",
                  "type": 2,
                  "start_time": "2024-02-21T08:49:58Z",
                  "duration": 60,
                  "timezone": "America/Los_Angeles",
                  "created_at": "2024-02-21T08:49:58Z",
                  "join_url": "https://us04web.zoom.us/j/78723497365?pwd=67MuOC4RaEDM5wbu1NajMVnvadebNY.1"
                },
                {
                  "uuid": "wNYtPAewS3+RYENNrgvQ9g==",
                  "id": 72781040142,
                  "host_id": "lqkrEKqMR1CCmALIVs73RQ",
                  "topic": "Test topic",
                  "type": 2,
                  "start_time": "2024-02-23T07:59:45Z",
                  "duration": 60,
                  "timezone": "Europe/London",
                  "created_at": "2024-02-23T06:59:45Z",
                  "join_url": "https://us04web.zoom.us/j/72781040142?pwd=95GC2CPDfDZiaMYDA1a31syE16N4ph.1"
                }
              ]
            }
        """.trimIndent()
        val expectedPage = parseJson<PaginationObject>(
            jsonResponse,
        ).toDomain()
        val page = call { meetings(jsonResponse).listScheduled(USER_ID) }
        assertEquals(expectedPage, page, "Unexpected page")
    }

    @Test
    fun `should handle meeting_created webhook`() = runBlocking {
        verifyWebhookHandler(MEETING_CREATED_TEST_EVENT, defaultMeetings::onMeetingCreated)
    }

    @Test
    fun `should handle meeting_started webhook`() = runBlocking {
        verifyWebhookHandler(MEETING_STARTED_TEST_EVENT, defaultMeetings::onMeetingStarted)
    }

    @Test
    fun `should handle meeting_ended webhook`() = runBlocking {
        verifyWebhookHandler(MEETING_ENDED_TEST_EVENT, defaultMeetings::onMeetingEnded)
    }

    @Test
    fun `should handle meeting_participant_joined webhook`() = runBlocking {
        verifyWebhookHandler(MEETING_PARTICIPANT_JOINED_TEST_EVENT, defaultMeetings::onMeetingParticipantJoined)
    }

    @Test
    fun `should handle meeting_participant_left webhook`() = runBlocking {
        verifyWebhookHandler(MEETING_PARTICIPANT_LEFT_TEST_EVENT, defaultMeetings::onMeetingParticipantLeft)
    }

    @Test
    fun `should ignore irrelevant webhook event`() = runBlocking {
        onWebhookRequest(MEETING_ENDED_TEST_EVENT) { call ->
            val result = defaultMeetings.onMeetingStarted(call) { event ->
                fail("Unexpected event: $event")
            }
            assertTrue(result.isSuccess, "Event should have been ignored")
        }
    }

    private suspend inline fun <reified T> verifyWebhookHandler(
        payload: String,
        crossinline action: suspend (ApplicationCall, (T) -> Unit) -> Result<Unit>
    ) {
        onWebhookRequest(payload) { call ->
            val result = action(call) { event ->
                val expectedEvent = json.decodeFromString<T>(payload)
                assertEquals(expectedEvent, event, "Unexpected event")
            }
            assertTrue(result.isSuccess, "Unexpected result")
        }
    }

    private fun meetings(responseBody: String? = null): Meetings =
        module(responseBody) { zoom, tokens ->
            zoom.meetings(tokens)
        }

    private suspend fun onWebhookRequest(payload: String, action: suspend (ApplicationCall) -> Unit) {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json(
                        json = Json {
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }
            routing {
                post("/webhook") {
                    action(call)
                }
            }
            client.post("/webhook") {
                contentType(ContentType.Application.Json)
                setBody(payload)
                header(DEFAULT_SIGNATURE_HEADER_NAME, validSignature(payload))
                header(DEFAULT_TIMESTAMP_HEADER_NAME, TIMESTAMP.toString())
            }
        }
    }
}
