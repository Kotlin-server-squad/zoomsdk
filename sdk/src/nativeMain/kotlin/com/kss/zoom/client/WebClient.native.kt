package com.kss.zoom.client

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

actual fun withContext(key: String, value: String): String? {
    return value
}

actual fun httpEngineFactory(): HttpClientEngineFactory<*> = CIO