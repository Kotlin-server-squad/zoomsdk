package com.kss.zoom.client

import com.kss.zoom.CallResult
import com.kss.zoom.CallResult.Failure
import com.kss.zoom.CallResult.Success
import com.kss.zoom.client.plugins.configureLogging
import com.kss.zoom.client.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*

class WebClient private constructor(val client: HttpClient) {
    companion object {
        const val FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded"

        fun create(client: HttpClient): WebClient = WebClient(client)

        fun create(): WebClient = WebClient(
            HttpClient(CIO) {
                configureLogging()
                configureSerialization()
            }
        )
    }

    suspend inline fun <reified T> post(
        url: String,
        contentType: String?,
        body: Any?
    ): CallResult<T> =
        runCoCatching {
            client.post(url) {
                contentType(contentType)
                body?.let { setBody(it) }
            }
        }


    suspend inline fun <reified T> post(
        url: String,
        token: String,
        contentType: String?,
        body: Any?
    ): CallResult<T> =
        runCoCatching {
            client.post(url) {
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
    ): CallResult<T> =
        runCoCatching {
            client.post(url) {
                contentType(ContentType.Application.Json)
                basicAuth(clientId, clientSecret)
                contentType(contentType)
                body?.let { setBody(it) }
            }.body()
        }

    fun HttpRequestBuilder.contentType(contentType: String?): HttpRequestBuilder {
        val parsedContentType = contentType?.let { ContentType.parse(it) } ?: ContentType.Application.Json
        contentType(parsedContentType)
        return this
    }

    suspend inline fun <reified T> runCoCatching(block: () -> HttpResponse): CallResult<T> {
        return try {
            block().let { response ->
                when (response.status.value) {
                    in 200..299 -> Success(response.body())
                    400 -> Failure.BadRequest(response.body())
                    401, 403 -> Failure.Unauthorized
                    404 -> Failure.NotFound
                    429 -> Failure.TooManyRequests
                    else -> Failure.Error(response.body())
                }
            }
        } catch (e: CancellationException) {
            // Re-throw the CancellationException to not treat it as an exceptional outcome
            throw e
        } catch (t: Throwable) {
            Failure.Error(t.message ?: "Unknown error")
        }
    }
}