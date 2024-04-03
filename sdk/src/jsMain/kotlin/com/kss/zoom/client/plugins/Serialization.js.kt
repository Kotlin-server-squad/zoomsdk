package com.kss.zoom.client.plugins

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlin.js.json

actual fun HttpClientConfig<*>.configureSerialization() {
    install(ContentNegotiation) {
        json(
            "json" to kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }
        )
    }
}