package com.kss.zoom.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*

/**
 * Adds a correlation id to the request if it is not already present.
 * @param headerName The name of the header to use for the correlation id.
 * @return The HttpClient with the correlation id added.
 */
fun HttpClient.withCorrelationId(headerName: String, generator: () -> String): HttpClient = this.config {
    install(DefaultRequest) {
        if (headers[headerName] == null) {
            header(headerName, generator())
        }
    }
}
