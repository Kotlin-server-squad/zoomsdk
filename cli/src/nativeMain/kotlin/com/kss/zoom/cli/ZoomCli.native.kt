package com.kss.zoom.cli

import io.ktor.client.engine.*
import io.ktor.client.engine.curl.*
import kotlinx.cinterop.toKString
import platform.posix.getenv

actual fun getSystemProperty(name: String): String? = getenv(name)?.toKString()

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = Curl