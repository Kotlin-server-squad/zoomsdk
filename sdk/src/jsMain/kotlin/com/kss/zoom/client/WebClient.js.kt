package com.kss.zoom.client

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

actual fun withContext(key: String, value: String): String? {
    // TODO There's no MDC in JS! Implement via continuation-local-storage, see: https://www.npmjs.com/package/cls-hooked
    return value
}

actual fun httpEngineFactory(): HttpClientEngineFactory<*> = Js