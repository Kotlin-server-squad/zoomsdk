package com.kss.zoom.sdk

import com.kss.zoom.Zoom
import com.kss.zoom.auth.model.AccessToken
import com.kss.zoom.auth.model.RefreshToken
import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.sdk.common.withCorrelationId
import com.kss.zoom.sdk.meetings.model.api.ScheduledMeetingResponse
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

abstract class ModuleTest<M : IZoomModule> {

    val objectMapper = Json {
        ignoreUnknownKeys = true
    }

    companion object {
        const val USER_ID = "userId"
        val CLIENT_TIMEZONE: TimeZone = TimeZone.getTimeZone(ZoneId.of("America/New_York"))
        val CONSTANT_CLOCK: Clock = Clock.fixed(Instant.ofEpochMilli(0), ZoneId.of("Z"))
    }

    private val capturedRequests: MutableList<HttpRequestData> = mutableListOf()

    abstract suspend fun sdkCall(module: M): Any

    abstract fun module(): M

    fun <M : IZoomModule> module(responseBody: String? = null, block: (Zoom, UserTokens) -> M): M {
        val zoom = Zoom.create(
            clientId = "clientId",
            clientSecret = "clientSecret",
            verificationToken = "test-token",
            httpClient = createHttpClient(responseBody)
        )
        val tokens = UserTokens(
            accessToken = AccessToken("accessToken", 3599),
            refreshToken = RefreshToken("refreshToken")
        )
        return block(zoom, tokens)
    }

    @AfterEach
    fun tearDown() {
        resetHttpClient()
    }

    @Test
    fun `should correctly set and reset correlation id`() {
        assertNull(MDC.get("correlationId"))
        val correlationId = "my-correlation-id"
        runBlocking {
            module().withCorrelationId(correlationId) {
                assert(MDC.get("correlationId") == correlationId)
            }
        }
        assertNull(MDC.get("correlationId"))
    }

    @Test
    fun `should propagate correlation id to http request`() {
        val correlationId = "my-correlation-id"
        runBlocking {
            module().withCorrelationId(correlationId) {
                sdkCall(this)
                assert(lastRequest()?.headers?.get("X-Correlation-Id") == correlationId)
            }
        }
    }

    private fun createHttpClient(responseBody: String?): HttpClient {
        return HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        serializersModule = SerializersModule {
                            contextual(ScheduledMeetingResponse::class, ScheduledMeetingResponse.serializer())
                        }
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

    private fun resetHttpClient() {
        capturedRequests.clear()
    }

    private fun lastRequest(): HttpRequestData? = capturedRequests.lastOrNull()

    @OptIn(InternalSerializationApi::class)
    inline fun <reified T : Any> parseJson(json: String): T {
        val serializer: KSerializer<T> = T::class.serializer()
        return objectMapper.decodeFromString(serializer, json)
    }
}