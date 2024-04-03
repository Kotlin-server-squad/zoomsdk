package com.kss.zoom.client

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import kotlin.test.assertEquals

fun HttpRequestData.expectUrl(url: String) {
    assertEquals(url, this.url.toString())
}

fun HttpRequestData.expectBearerToken(token: String) {
    val authHeader = this.headers["Authorization"]
    assertEquals("Bearer $token", authHeader, "Request bearer token doesn't match")
}

fun HttpRequestData.expectBasicAuth(username: String, password: String) {
    val authHeader = this.headers["Authorization"]
    assertEquals("Basic ${"$username:$password".encodeBase64()}", authHeader, "Request bearer token doesn't match")
}

fun HttpRequestData.expectPayload(payload: String, contentType: String? = null) {
    val body = if (this.body is TextContent) this.body as TextContent else null
    assertEquals(payload, body?.text, "Request body doesn't match")
    contentType?.let {
        assertEquals(it, body?.contentType.toString(), "Request content type doesn't match")
    }
}

fun HttpRequestData.expectPost() {
    expectMethod(HttpMethod.Post)
}

fun HttpRequestData.expectGet() {
    expectMethod(HttpMethod.Get)
}

fun HttpRequestData.expectDelete() {
    expectMethod(HttpMethod.Delete)
}

private fun HttpRequestData.expectMethod(method: HttpMethod) {
    assertEquals(method, this.method, "Request method doesn't match")
}