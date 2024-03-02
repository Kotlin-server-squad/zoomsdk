package com.kss.zoom.sdk

import com.kss.zoom.sdk.ZoomMock.CLIENT_TIMEZONE
import com.kss.zoom.sdk.ZoomMock.CONSTANT_CLOCK
import com.kss.zoom.sdk.ZoomMock.USER_ID
import com.kss.zoom.sdk.ZoomMock.module
import com.kss.zoom.utils.call
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class MeetingsTest : ModuleTest<Meetings>() {
    override fun module(): Meetings = meetings()
    override suspend fun sdkCall(module: Meetings): Any =
        module.listScheduled(USER_ID)

    @Test
    fun `should schedule meeting`() = runBlocking {
        val meeting = call {
            meetings(
                """
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
            ).create(
                userId = USER_ID,
                topic = "My Meeting",
                startTime = LocalDateTime.now(CONSTANT_CLOCK),
                duration = 60,
                timezone = CLIENT_TIMEZONE
            )
        }
        assertNotNull(meeting, "Expected meeting to be created")
    }

    private fun meetings(responseBody: String? = null): Meetings =
        module(responseBody) { zoom, tokens ->
            zoom.meetings(tokens)
        }

}