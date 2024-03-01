package com.kss.zoom.sdk

import com.kss.zoom.Zoom
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

abstract class ZoomModuleTestBase {
    val zoom = Zoom.create("clientId", "clientSecret", createHttpClient())

    private fun createHttpClient(): HttpClient {
        return HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json()
            }
            engine {
                addHandler { request ->
                    respond("OK")
                }
            }
        }
    }
}