package com.kss.zoom.cli

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

actual fun getSystemProperty(name: String): String? = System.getProperty(name)

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = CIO