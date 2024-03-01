package com.kss.zoom.sdk

import com.kss.zoom.auth.AccessToken
import com.kss.zoom.auth.RefreshToken
import com.kss.zoom.auth.UserTokens
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC

class MeetingsTest : ZoomModuleTestBase() {

    private lateinit var meetings: Meetings

    @BeforeEach
    fun setUp() {
        meetings = zoom.meetings(
            UserTokens(
                accessToken = AccessToken("accessToken", 3599),
                refreshToken = RefreshToken("refreshToken")
            )
        )
    }

    @Test
    fun `should correctly set and reset correlation id`() {
        assertNull(MDC.get("correlationId"))
        runBlocking {
            meetings.withCorrelationId("id") {
                assert(MDC.get("correlationId") == "id")
            }
        }
        assertNull(MDC.get("correlationId"))
    }

}