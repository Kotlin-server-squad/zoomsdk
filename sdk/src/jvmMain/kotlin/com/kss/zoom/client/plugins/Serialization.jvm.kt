package com.kss.zoom.client.plugins

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

actual fun HttpClientConfig<*>.configureSerialization() {
    install(ContentNegotiation) {
        json(
            json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }
        )
    }
}