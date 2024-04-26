package com.kss.zoom.sdk

import com.kss.zoom.Zoom
import com.kss.zoom.auth.model.AccessToken
import com.kss.zoom.auth.model.RefreshToken
import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.sdk.meetings.model.api.ScheduledMeetingResponse
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.test.AfterTest

abstract class ModuleTest<M : IZoomModule> {
    val jsonMapper = Json {
        ignoreUnknownKeys = true
    }

    private val capturedRequests: MutableList<HttpRequestData> = mutableListOf()

    @AfterTest
    fun tearDown() {
        resetHttpClient()
    }

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

    @OptIn(InternalSerializationApi::class)
    inline fun <reified T : Any> parseJson(json: String): T {
        val serializer: KSerializer<T> = T::class.serializer()
        return jsonMapper.decodeFromString(serializer, json)
    }

    suspend inline fun <reified T : Any> parseJson(request: HttpRequestData): T {
        return parseJson(request.body.toByteReadPacket().readText())
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

    protected fun assertRequestSent(path: String, method: HttpMethod): HttpRequestData {
        val request = capturedRequests.firstOrNull { it.url.encodedPath == path && it.method == method }
        if (request == null) {
            throw AssertionError("Request not sent: $method $path")
        }
        return request
    }

    private fun resetHttpClient() {
        capturedRequests.clear()
    }

}