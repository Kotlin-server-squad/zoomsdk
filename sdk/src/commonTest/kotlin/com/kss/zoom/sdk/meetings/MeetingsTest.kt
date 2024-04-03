package com.kss.zoom.sdk.meetings

import com.kss.zoom.auth.model.AccessToken
import com.kss.zoom.auth.model.RefreshToken
import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.mockEngine
import com.kss.zoom.sdk.common.*
import com.kss.zoom.sdk.webhooks.WebhookVerifier
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import kotlin.test.*

class MeetingsTest {

    companion object {
        private const val USER_ID = "lqkrEKqMR1CCmALIVs73RQ"
        private const val MEETING_ID = 78723497365L
        private val MEETING_API_RESPONSE =
            """
                {
                  "uuid": "dzFrpLGqRcOtpiQQeAVDVA==",
                  "id": $MEETING_ID,
                  "host_id": "$USER_ID",
                  "host_email": "test@test.com",
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
                  "encrypted_password": "67MuOC4RaEDM5wbu1NajMVnvadebNY.1",
                  "pre_schedule": false
                }
            """.trimIndent()

        private val SCHEDULED_MEETINGS_API_RESPONSE =
            """
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
                      "uuid": "sEpzyWQKR0aJemyAAk8N0A==",
                      "id": 75614069732,
                      "host_id": "lqkrEKqMR1CCmALIVs73RQ",
                      "topic": "Test topic",
                      "type": 2,
                      "start_time": "2024-02-22T21:03:47Z",
                      "duration": 60,
                      "timezone": "Europe/London",
                      "created_at": "2024-02-22T21:03:47Z",
                      "join_url": "https://us04web.zoom.us/j/75614069732?pwd=c576bKRbfJIVSVqaBqwQwJvbAtvNLG.1"
                    }
                  ]
                }
            """.trimIndent()
    }

    private lateinit var httpClient: HttpClient
    private lateinit var meetings: IMeetings

    @BeforeTest
    fun setUp() {
        httpClient = HttpClient(mockEngine { requestData ->
            when (requestData.url.encodedPath) {
                "/v2/users/$USER_ID/meetings" -> {
                    when (requestData.url.encodedQuery) {
                        "type=scheduled&page_number=1&page_size=30" -> SCHEDULED_MEETINGS_API_RESPONSE
                        else -> MEETING_API_RESPONSE
                    }
                }
                "/v2/meetings/$MEETING_ID" -> MEETING_API_RESPONSE
                else -> null
            }
        }) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        val userTokens = UserTokens(
            AccessToken("access-token", 3600),
            RefreshToken("refresh-token")
        )
        meetings = Meetings.create(httpClient = httpClient, tokens = userTokens)
    }

    @Test
    fun shouldCreateDefaultMeetingsInstance() {
        val meetings = Meetings.create() as Meetings
        assertNotNull(meetings.webClient, "Web client should not be null")
    }

    @Test
    fun shouldCreateMeetingsInstanceWithCustomHttpClient() {
        val httpClient = HttpClient()
        val meetings = Meetings.create(httpClient = httpClient) as Meetings
        assertEquals(httpClient, meetings.webClient.httpClient, "HTTP client should be the same as the custom one")
    }

    @Test
    fun shouldCreateMeetingsInstanceWithCustomWebhookVerifier() {
        val webhookVerifier = WebhookVerifier("secret")
        val meetings = Meetings.create(webhookVerifier = webhookVerifier) as Meetings
        assertEquals(webhookVerifier, meetings.webhookVerifier, "Webhook verifier should be the same as the custom one")
    }

    @Test
    fun shouldCreateMeeting() = runTest {
        val meeting = call {
            meetings.create(
                userId = USER_ID,
                topic = "Your Meeting Title",
                startTime = "2024-02-21T08:49:58Z".zonedToLocalDateTime(),
                duration = 60,
                timezone = TimeZone.of("America/Los_Angeles")
            )
        }
        assertEquals("2024-02-21T08:49:58", meeting.startTime.toIsoString(), "Start time should match")
        assertEquals("America/Los_Angeles", meeting.timeZone?.id, "Timezone should match")
        assertEquals(60, meeting.duration, "Duration should match")
        assertEquals("Your Meeting Title", meeting.topic, "Topic should match")
    }

    @Test
    fun shouldGetMeetingById() = runTest {
        val meeting = call { meetings.get(MEETING_ID) }
        assertEquals(MEETING_ID, meeting.id, "Meeting ID should match")
    }

    @Test
    fun shouldDeleteMeetingById() = runTest {
        val result = call { meetings.delete(MEETING_ID) }
        assertTrue(result, "Meeting should be deleted")
    }

    @Test
    fun shouldDeleteAllMeetings() = runTest {
        val result = call { meetings.deleteAll(USER_ID) }
        assertTrue(result, "All meetings should be deleted")
    }

    @Test
    fun shouldListScheduledMeetings() = runTest {
        val result = call { meetings.listScheduled(USER_ID) }
        assertEquals(3, result.items.size, "Expected number of meetings should match")
    }
}