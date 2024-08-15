package com.kss.zoom.client

import com.kss.zoom.client.plugins.configureLogging
import com.kss.zoom.client.plugins.configureSerialization
import com.kss.zoom.model.CallResult
import io.ktor.client.*
import io.ktor.client.engine.cio.*

interface ApiClient {
    companion object {
        const val DEFAULT_CORRELATION_ID_HEADER = "X-Correlation-Id"
        const val DEFAULT_BASE_URL = "https://api.zoom.us/v2"

        fun instance(baseUrl: String = DEFAULT_BASE_URL): ApiClient {
            return DefaultApiClient(
                baseUrl,
                HttpClient(CIO) {
                    configureLogging()
                    configureSerialization()
                },
                DEFAULT_CORRELATION_ID_HEADER
            )
        }
    }

    suspend fun <T> get(
        path: String,
        token: String,
    ): CallResult<T>

    suspend fun <T> post(
        url: String,
        clientId: String,
        clientSecret: String,
        contentType: String,
        body: Any?
    ): CallResult<T>

    suspend fun <T> post(
        url: String,
        body: String,
        contentType: String
    ): CallResult<T>

    suspend fun <T> post(
        path: String,
        token: String,
    ): CallResult<T>

    suspend fun <T> post(
        path: String,
        token: String,
        contentType: String,
        body: Any,
    ): CallResult<T>

    suspend fun <T> patch(
        path: String,
        token: String,
    ): CallResult<T>

    suspend fun <T> patch(
        path: String,
        token: String,
        contentType: String,
        body: Any,
    ): CallResult<T>

    suspend fun <T> delete(
        path: String,
        token: String,
    ): CallResult<T>
}

private class DefaultApiClient(
    private val baseUrl: String,
    private val httpClient: HttpClient,
    private val correlationIdHeader: String,
) : ApiClient {

    override suspend fun <T> get(path: String, token: String): CallResult<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> post(
        url: String,
        clientId: String,
        clientSecret: String,
        contentType: String,
        body: Any?,
    ): CallResult<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> post(url: String, body: String, contentType: String): CallResult<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> post(path: String, token: String): CallResult<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> post(path: String, token: String, contentType: String, body: Any): CallResult<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> patch(path: String, token: String): CallResult<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> patch(path: String, token: String, contentType: String, body: Any): CallResult<T> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> delete(path: String, token: String): CallResult<T> {
        TODO("Not yet implemented")
    }
}
