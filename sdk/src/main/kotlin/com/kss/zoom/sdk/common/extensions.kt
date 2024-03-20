package com.kss.zoom.sdk.common

import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.IZoomModule
import io.ktor.client.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

suspend fun <M : IZoomModule, T> M.withCorrelationId(id: String, block: suspend M.() -> T): T {
    return withMDCContext(mapOf("correlationId" to id)) {
        this.block()
    }
}

fun HttpClient?.toWebClient(): WebClient =
    this?.let { WebClient.create(it) } ?: WebClient.create()

fun LocalDateTime.toIsoString(): String =
    this.format(DateTimeFormatter.ISO_DATE_TIME)
