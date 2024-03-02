package com.kss.zoom.sdk

import com.kss.zoom.Zoom
import com.kss.zoom.auth.AccessToken
import com.kss.zoom.auth.RefreshToken
import com.kss.zoom.auth.UserTokens
import com.kss.zoom.sdk.model.ZoomModule
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

object ZoomMock {
    const val USER_ID = "userId"
    val CLIENT_TIMEZONE: TimeZone = TimeZone.getTimeZone(ZoneId.of("America/New_York"))
    val CONSTANT_CLOCK: Clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.of("Z"))

    private val capturedRequests: MutableList<HttpRequestData> = mutableListOf()

    fun <M : ZoomModule> module(responseBody: String? = null, block: (Zoom, UserTokens) -> M): M {
        val zoom = Zoom.create("clientId", "clientSecret", createHttpClient(responseBody))
        val tokens = UserTokens(
            accessToken = AccessToken("accessToken", 3599),
            refreshToken = RefreshToken("refreshToken")
        )
        return block(zoom, tokens)
    }

    fun resetHttpClient() {
        capturedRequests.clear()
    }

    fun lastRequest(): HttpRequestData? = capturedRequests.lastOrNull()

    private fun createHttpClient(responseBody: String?): HttpClient {
        return HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(
                    json = kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            engine {
                addHandler { request ->
                    capturedRequests.add(request)
                    respond(
                        content = responseBody ?: "{}",
                        headers = headersOf("Content-Type" to listOf("application/json"))
                    )
                }
            }
        }
    }
}