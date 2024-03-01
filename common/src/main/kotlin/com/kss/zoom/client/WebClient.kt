package com.kss.zoom.client

import com.kss.zoom.ZoomException
import com.kss.zoom.client.plugins.configureLogging
import com.kss.zoom.client.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import org.slf4j.MDC
import java.util.*

class WebClient private constructor(val client: HttpClient, val correlationIdHeader: String) {
    companion object {
        const val FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded"
        const val JSON_CONTENT_TYPE = "application/json"
        private const val DEFAULT_CORRELATION_ID_HEADER = "X-Correlation-Id"

        fun create(client: HttpClient, correlationIdHeader: String = DEFAULT_CORRELATION_ID_HEADER): WebClient =
            WebClient(client.withCorrelationId(correlationIdHeader, ::generateCorrelationId), correlationIdHeader)

        fun create(correlationIdHeader: String = DEFAULT_CORRELATION_ID_HEADER): WebClient = WebClient(
            HttpClient(CIO) {
                configureLogging()
                configureSerialization()
            }.withCorrelationId(correlationIdHeader, ::generateCorrelationId),
            correlationIdHeader
        )

        private fun generateCorrelationId(): String = UUID.randomUUID().toString()
    }

    suspend inline fun <reified T> post(
        url: String,
        contentType: String?,
        body: Any?
    ): Result<T> =
        runCoCatching {
            client.post(url) {
                header(correlationIdHeader, getOrCreateCorrelationId())
                contentType(contentType)
                body?.let { setBody(it) }
            }
        }

    suspend inline fun <reified T> post(
        url: String,
        token: String,
        contentType: String?,
        body: Any?
    ): Result<T> =
        runCoCatching {
            client.post(url) {
                header(correlationIdHeader, getOrCreateCorrelationId())
                bearerAuth(token)
                contentType(contentType)
                body?.let { setBody(it) }
            }.body()
        }

    suspend inline fun <reified T> post(
        url: String,
        clientId: String,
        clientSecret: String,
        contentType: String?,
        body: Any?
    ): Result<T> =
        runCoCatching {
            client.post(url) {
                header(correlationIdHeader, getOrCreateCorrelationId())
                basicAuth(clientId, clientSecret)
                contentType(contentType)
                body?.let { setBody(it) }
            }.body()
        }

    suspend inline fun <reified T> get(url: String, token: String): Result<T> =
        runCoCatching {
            client.get(url) {
                header(correlationIdHeader, getOrCreateCorrelationId())
                bearerAuth(token)
            }
        }

    suspend inline fun delete(url: String, token: String): Result<Unit> =
        runCoCatching {
            client.delete(url) {
                header(correlationIdHeader, getOrCreateCorrelationId())
                bearerAuth(token)
            }
        }

    fun HttpRequestBuilder.contentType(contentType: String?): HttpRequestBuilder {
        val parsedContentType = contentType?.let { ContentType.parse(it) } ?: ContentType.Application.Json
        contentType(parsedContentType)
        return this
    }

    suspend inline fun <reified T> runCoCatching(block: () -> HttpResponse): Result<T> {
        return try {
            block().let { response ->
                when (response.status.value) {
                    in 200..299 -> Result.success(response.body())
                    else -> {
                        val message = when (response.status.value) {
                            400 -> "Bad request: ${response.bodyAsText()}"
                            401, 403 -> "Unauthorized access to the resource."
                            404 -> "Not found"
                            429 -> "Too many requests"
                            else -> "Error: ${response.bodyAsText()}"
                        }
                        Result.failure(ZoomException(response.status.value, message))
                    }
                }
            }
        } catch (e: CancellationException) {
            // Re-throw the CancellationException to not treat it as an exceptional outcome
            throw e
        } catch (t: Throwable) {
            Result.failure(
                ZoomException(
                    HttpStatusCode.InternalServerError.value,
                    t.message ?: "Internal server error"
                )
            )
        }
    }

    fun getOrCreateCorrelationId(): String {
        return MDC.get("correlationId") ?: generateCorrelationId()
    }
}