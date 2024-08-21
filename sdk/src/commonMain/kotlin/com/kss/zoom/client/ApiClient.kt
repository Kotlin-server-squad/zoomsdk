package com.kss.zoom.client

import com.kss.zoom.client.plugins.configureLogging
import com.kss.zoom.client.plugins.configureSerialization
import com.kss.zoom.model.CallResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*


class ApiClient(
    val httpClient: HttpClient,
    val correlationIdHeader: String = DEFAULT_CORRELATION_ID_HEADER,
) {
    companion object {
        const val DEFAULT_CORRELATION_ID_HEADER = "X-Correlation-Id"

        val DEFAULT = ApiClient(
            HttpClient {
                configureLogging()
                configureSerialization()
            },
            DEFAULT_CORRELATION_ID_HEADER
        )
    }

    suspend inline fun <reified T> get(url: String, token: String): CallResult<T> {
        return tryHttpCall {
            httpClient.get(url) {
                bearerAuth(token)
            }
        }
    }

    suspend inline fun <reified T> post(
        url: String,
        clientId: String,
        clientSecret: String,
        contentType: String,
        body: Any?,
    ): CallResult<T> {
        return tryHttpCall {
            httpClient.post(url) {
                basicAuth(clientId, clientSecret)
                contentType(ContentType.parse(contentType))
                body?.let { setBody(it) }
            }
        }
    }

    suspend inline fun <reified T> post(url: String, body: String, contentType: String): CallResult<T> {
        return tryHttpCall {
            httpClient.post(url) {
                contentType(ContentType.parse(contentType))
                setBody(body)
            }
        }
    }

    suspend inline fun <reified T> post(url: String, token: String, contentType: String, body: Any): CallResult<T> {
        return tryHttpCall {
            httpClient.post(url) {
                bearerAuth(token)
                contentType(ContentType.parse(contentType))
                setBody(body)
            }
        }
    }

    suspend inline fun <reified T> patch(url: String, token: String, contentType: String, body: Any): CallResult<T> {
        return tryHttpCall {
            httpClient.patch(url) {
                bearerAuth(token)
                contentType(ContentType.parse(contentType))
                setBody(body)
            }
        }
    }

    suspend inline fun <reified T> delete(url: String, token: String): CallResult<T> {
        return tryHttpCall {
            httpClient.delete(url) {
                bearerAuth(token)
            }
        }
    }
}

