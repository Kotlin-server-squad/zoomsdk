package com.kss.zoomsdk.client

import com.kss.zoomsdk.client.plugins.configureLogging
import com.kss.zoomsdk.client.plugins.configureSerialization
import com.kss.zoomsdk.model.CallResult
import io.ktor.client.*
import io.ktor.client.engine.cio.*

interface ApiClient {
    companion object {
        const val DEFAULT_CORRELATION_ID_HEADER = "X-Correlation-Id"
        fun instance(baseUrl: String): ApiClient {
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
    ): T

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

    override suspend fun <T> get(path: String, token: String): T {
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
