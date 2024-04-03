package com.kss.zoom.client

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class WebClientTest {
    companion object {
        private const val TEST_URL = "https://zoom.us"
    }

    @Test
    fun shouldHandlePost() = runTest {
        val result = webClient { request ->
            request.expectPost()
            request.expectUrl(TEST_URL)
            request.expectPayload("test", "application/json")
        }.post<Unit>(
            url = TEST_URL,
            contentType = "application/json",
            body = "test"
        )
        assertTrue(
            result.isSuccess,
            "Should handle json post"
        )
    }

    @Test
    fun shouldHandlePostWithBearerToken() = runTest {
        val result = webClient { request ->
            request.expectPost()
            request.expectUrl(TEST_URL)
            request.expectBearerToken("token")
            request.expectPayload("test", "application/json")
        }.post<Unit>(
            url = TEST_URL,
            token = "token",
            contentType = "application/json",
            body = "test"
        )
        assertTrue(
            result.isSuccess,
            "Should handle json post"
        )
    }

    @Test
    fun shouldHandlePostWithBasicAuth() = runTest {
        val result = webClient { request ->
            request.expectPost()
            request.expectUrl(TEST_URL)
            request.expectBasicAuth("clientId", "clientSecret")
            request.expectPayload("test", "application/json")
        }.post<Unit>(
            url = TEST_URL,
            clientId = "clientId",
            clientSecret = "clientSecret",
            contentType = "application/json",
            body = "test"
        )
        assertTrue(
            result.isSuccess,
            "Should handle json post"
        )
    }

    @Test
    fun shouldHandleGet() = runTest {
        val result = webClient { request ->
            request.expectGet()
            request.expectUrl(TEST_URL)
            request.expectBearerToken("token")
        }.get<Unit>(
            url = TEST_URL,
            token = "token"
        )
        assertTrue(
            result.isSuccess,
            "Should handle json post"
        )
    }

    @Test
    fun shouldHandleDelete() = runTest {
        val result = webClient { request ->
            request.expectDelete()
            request.expectUrl(TEST_URL)
            request.expectBearerToken("token")
        }.delete(
            url = TEST_URL,
            token = "token"
        )
        assertTrue(
            result.isSuccess,
            "Should handle json post"
        )
    }

    @Test
    fun shouldGenerateCorrelationIdIfNoneIsProvided() = runTest {
        val webClient = webClient { request ->
            assertTrue(
                request.headers.contains(WebClient.DEFAULT_CORRELATION_ID_HEADER),
                "Should generate correlation id if none is provided"
            )
        }

        listOf(
            webClient.post<Unit>(TEST_URL, null, "test"),
            webClient.get<Unit>(TEST_URL, "test"),
            webClient.delete(
                TEST_URL, "test"
            )
        ).forEach { result ->
            assertTrue(
                result.isSuccess,
                "Should generate correlation id if none is provided"
            )
        }
    }

    private fun webClient(validator: (HttpRequestData) -> Unit): WebClient {
        return WebClient(httpClient(validator))
    }
}

private fun httpClient(validator: (HttpRequestData) -> Unit): HttpClient = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            validator(request)
            respondOk()
        }
    }
}