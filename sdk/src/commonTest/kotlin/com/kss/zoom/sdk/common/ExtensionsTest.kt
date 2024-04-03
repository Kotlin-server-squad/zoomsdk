package com.kss.zoom.sdk.common

import io.ktor.client.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ExtensionsTest {

    @Test
    fun shouldConvertHttpClientToWebClient() {
        val httpClient = HttpClient()
        val webClient = httpClient.toWebClient()
        assertEquals(httpClient, webClient.httpClient, "WebClient should have the same HttpClient instance")
    }

    @Test
    fun shouldCreateDefaultWebClient() {
        val webClient = null.toWebClient()
        assertNotNull(webClient.httpClient, "WebClient should have a default HttpClient instance")
    }

    @Test
    fun shouldConvertStringToTimeZone() {
        val timeZone = "UTC".toTimeZone()
        assertNotNull(timeZone, "Time zone should be created")
    }

    @Test
    fun shouldConvertLocalDateTimeToIsoString() {
        val localDateTime = LocalDateTime(2024, 1, 1, 0, 0)
        val isoString = localDateTime.toIsoString()
        assertEquals("2024-01-01T00:00", isoString, "LocalDateTime should be converted to ISO string")
    }

    @Test
    fun shouldConvertStringToLocalDateTime() {
        val isoString = "2024-01-01T00:00"
        val localDateTime = isoString.toLocalDateTime()
        assertEquals(LocalDateTime(2024, 1, 1, 0, 0), localDateTime, "ISO string should be converted to LocalDateTime")
    }

    @Test
    fun shouldConvertStringToInstant() {
        val isoString = "2024-01-01T00:00:00Z"
        val instant = isoString.toInstant()
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), instant, "ISO string should be converted to Instant")
    }
}