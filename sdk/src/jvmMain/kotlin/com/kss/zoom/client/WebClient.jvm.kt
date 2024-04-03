package com.kss.zoom.client

import io.github.oshai.kotlinlogging.withLoggingContext
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import org.slf4j.MDC

actual fun withContext(key: String, value: String): String? =
    try {
        withLoggingContext(key to value) {
            MDC.get(key)
        }
    } catch (e: IllegalArgumentException) {
        null
    }

actual fun httpEngineFactory(): HttpClientEngineFactory<*> = CIO
