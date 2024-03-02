package com.kss.zoom.sdk

import com.kss.zoom.sdk.model.MeetingResponse
import com.kss.zoom.sdk.model.PaginationObject
import com.kss.zoom.sdk.model.toModel
import com.kss.zoom.utils.call
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class MeetingsTest : ModuleTest<Meetings>() {
    override fun module(): Meetings = meetings()
    override suspend fun sdkCall(module: Meetings): Any =
        module.listScheduled(USER_ID)

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
        val expectedMeeting = parseJson(jsonResponse, MeetingResponse::class).toModel()
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
        val expectedMeeting = parseJson(jsonResponse, MeetingResponse::class).toModel()
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
        val expectedPage = parseJson(jsonResponse, PaginationObject::class).toModel()
        val page = call { meetings(jsonResponse).listScheduled(USER_ID) }
        assertEquals(expectedPage, page, "Unexpected page")
    }

    private fun meetings(responseBody: String? = null): Meetings =
        module(responseBody) { zoom, tokens ->
            zoom.meetings(tokens)
        }

}