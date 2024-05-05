package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

// SSL requires Curl engine, blocked by an issue with curl: https://youtrack.jetbrains.com/issue/KTOR-6361
actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = CIO

actual fun terminalManager(terminal: Terminal): TerminalManager = LinuxX64TerminalManager(terminal)