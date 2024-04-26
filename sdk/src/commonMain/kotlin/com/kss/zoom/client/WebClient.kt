package com.kss.zoom.client

import com.kss.zoom.auth.model.ZoomException
import com.kss.zoom.client.plugins.configureLogging
import com.kss.zoom.client.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.coroutines.cancellation.CancellationException

class WebClient(
    val httpClient: HttpClient,
    val correlationIdHeader: String = DEFAULT_CORRELATION_ID_HEADER
) {
    constructor(correlationIdHeader: String = DEFAULT_CORRELATION_ID_HEADER) : this(
        defaultHttpClient(correlationIdHeader), correlationIdHeader
    )

    companion object {
        const val FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded"
        const val JSON_CONTENT_TYPE = "application/json"
        const val DEFAULT_CORRELATION_ID_HEADER = "X-Correlation-Id"
        const val CORRELATION_ID_KEY = "correlationId"

        fun defaultHttpClient(correlationIdHeader: String): HttpClient {
            return HttpClient(httpEngineFactory()) {
                configureLogging()
                configureSerialization()
            }.withCorrelationId(correlationIdHeader, ::generateRandomString)
        }
    }

    suspend inline fun <reified T> post(
        url: String,
        contentType: String?,
        body: Any?
    ): Result<T> =
        runCoCatching {
            httpClient.post(url) {
                header(correlationIdHeader, withCorrelationId())
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
            httpClient.post(url) {
                header(correlationIdHeader, withCorrelationId())
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
            httpClient.post(url) {
                header(correlationIdHeader, withCorrelationId())
                basicAuth(clientId, clientSecret)
                contentType(contentType)
                body?.let { setBody(it) }
            }.body()
        }

    suspend inline fun <reified T> get(url: String, token: String): Result<T> =
        runCoCatching {
            httpClient.get(url) {
                header(correlationIdHeader, withCorrelationId())
                bearerAuth(token)
            }
        }

    suspend inline fun <reified T> patch(
        url: String,
        token: String,
        contentType: String?,
        body: Any?
    ): Result<T> =
        runCoCatching {
            httpClient.patch(url) {
                header(correlationIdHeader, withCorrelationId())
                bearerAuth(token)
                contentType(contentType)
                body?.let { setBody(it) }
            }.body()
        }

    suspend inline fun delete(url: String, token: String): Result<Unit> =
        runCoCatching {
            httpClient.delete(url) {
                header(correlationIdHeader, withCorrelationId())
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

    fun withCorrelationId(): String? =
        withContext(CORRELATION_ID_KEY, generateRandomString())
}

expect fun withContext(key: String, value: String): String?

expect fun httpEngineFactory(): HttpClientEngineFactory<*>
