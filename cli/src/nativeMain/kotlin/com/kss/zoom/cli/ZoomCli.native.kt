package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.engine.*
import io.ktor.client.engine.curl.*
import kotlinx.cinterop.*
import platform.posix.*

actual fun getSystemProperty(name: String): String? = getenv(name)?.toKString()

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = Curl

actual fun terminalManager(terminal: Terminal): TerminalManager = NativeTerminalManager(terminal)