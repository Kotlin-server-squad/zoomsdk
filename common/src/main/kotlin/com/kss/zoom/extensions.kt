package com.kss.zoom

import com.kss.zoom.client.WebClient
import io.ktor.client.*

fun HttpClient?.toWebClient(): WebClient =
    this?.let { WebClient.create(it) } ?: WebClient.create()
