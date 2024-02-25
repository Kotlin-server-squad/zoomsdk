package com.kss.zoom

import com.kss.zoom.client.WebClient
import io.ktor.client.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun HttpClient?.toWebClient(): WebClient =
    this?.let { WebClient.create(it) } ?: WebClient.create()

fun LocalDateTime.toIsoString(): String =
    this.format(DateTimeFormatter.ISO_DATE_TIME)