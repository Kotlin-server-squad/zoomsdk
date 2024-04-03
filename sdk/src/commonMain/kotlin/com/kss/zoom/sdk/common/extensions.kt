package com.kss.zoom.sdk.common

import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.IZoomModule
import io.ktor.client.*
import kotlinx.datetime.*

suspend fun <M : IZoomModule, T> M.withCorrelationId(id: String, block: suspend M.() -> T): T {
    return withMDCContext(mapOf("correlationId" to id)) {
        this.block()
    }
}

fun HttpClient?.toWebClient(): WebClient =
    this?.let { WebClient(it) } ?: WebClient()

fun String?.toTimeZone(): TimeZone? =
    this?.let {
        try {
            TimeZone.of(it)
        } catch (e: IllegalTimeZoneException) {
            null
        }
    }

fun LocalDateTime.toIsoString(): String =
    this.toString()

fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this)

fun String.toInstant(): Instant =
    Instant.parse(this)

fun String.zonedToLocalDateTime(): LocalDateTime =
    Instant.parse(this).toLocalDateTime(TimeZone.UTC)
