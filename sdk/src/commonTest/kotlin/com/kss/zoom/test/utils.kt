package com.kss.zoom.test

import com.kss.zoom.client.ApiClient
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

val testClock = TestClock()

suspend fun <T> withMockClient(
    engine: HttpClientEngine = MockEngine {
        respondOk()
    },
    block: suspend (ApiClient) -> T,
) {
    val httpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }
    }
    block(ApiClient(httpClient))
}

