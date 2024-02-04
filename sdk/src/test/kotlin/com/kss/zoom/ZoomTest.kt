package com.kss.zoom

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class ZoomTest {

    lateinit var httpClient: HttpClient

    @BeforeEach
    fun setup() {
        httpClient = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respondOk()
                }
            }
        }
    }

    @Test
    fun `should load`() {
        val zoom = Zoom.create("clientId", "clientSecret")
        assertNotNull(zoom.auth())
        assertNotNull(zoom.meetings())
    }

    @Test
    fun `should load with custom http client`() {
        val zoom = Zoom.create("clientId", "clientSecret", httpClient)
        assertNotNull(zoom.auth())
        assertNotNull(zoom.meetings())
    }

    @Test
    fun `should be modular`() {
        val meetings = Zoom.meetings("clientId", "clientSecret")
        assertNotNull(meetings.auth())
    }

    @Test
    fun `should be modular with custom http client`() {
        val meetings = Zoom.meetings("clientId", "clientSecret", httpClient)
        assertNotNull(meetings.auth())
    }
}