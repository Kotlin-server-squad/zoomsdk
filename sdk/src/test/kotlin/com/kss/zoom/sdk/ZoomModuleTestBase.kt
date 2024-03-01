package com.kss.zoom.sdk

import com.kss.zoom.Zoom
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

abstract class ZoomModuleTestBase {
    companion object {
        const val USER_ID = "userId"
    }
    private val capturedRequests: MutableList<HttpRequestData> = mutableListOf()

    val zoom = Zoom.create("clientId", "clientSecret", createHttpClient())

    protected fun resetHttpClient() {
        capturedRequests.clear()
    }

    protected fun lastRequest(): HttpRequestData? = capturedRequests.lastOrNull()

    private fun createHttpClient(): HttpClient {
        return HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json()
            }
            engine {
                addHandler { request ->
                    capturedRequests.add(request)
                    respond("OK")
                }
            }
        }
    }
}