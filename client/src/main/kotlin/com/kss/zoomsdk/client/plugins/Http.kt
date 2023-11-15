package com.kss.zoomsdk.client.plugins

import com.kss.zoomsdk.client.ZoomClientConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.util.*

class Http(config: ZoomClientConfig, val client: HttpClient) {
    companion object {
        // Media types
        const val FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded"

        // Headers
        const val AUTHORIZATION_HEADER = "Authorization"
        const val CONTENT_TYPE_HEADER = "Content-Type"
    }

    private val authToken: String = Base64.getEncoder().encodeToString(
        "${config.clientId}:$${config.clientSecret}".encodeToByteArray()
    )

    suspend inline fun <reified T> post(
        url: String,
        contentType: String,
        body: Any?,
        accessToken: String? = null
    ): Result<T> {
        return runCatching {
            client.post(url) {
                header(AUTHORIZATION_HEADER, authHeader(accessToken))
                header(CONTENT_TYPE_HEADER, contentType)
                body?.let { setBody(it) }
            }
        }.map { it.body() }
    }

    fun authHeader(accessToken: String? = null): String =
        accessToken?.let { "Bearer $it" } ?: "Basic $authToken"
}