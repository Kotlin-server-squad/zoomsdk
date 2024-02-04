package com.kss.zoom.client

import com.kss.zoom.client.plugins.configureLogging
import com.kss.zoom.client.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
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
    ): Result<T> =
        runCoCatching {
            client.post(url) {
                contentType(contentType)
                body?.let { setBody(it) }
            }.body()
        }

    suspend inline fun <reified T> post(
        url: String,
        token: String,
        contentType: String?,
        body: Any?
    ): Result<T> =
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
    ): Result<T> =
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

    suspend fun <T, R> T.runCoCatching(block: suspend T.() -> R): Result<R> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            // Re-throw the CancellationException to not treat it as an exceptional outcome
            throw e
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}