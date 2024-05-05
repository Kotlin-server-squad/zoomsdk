package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.engine.*
import io.ktor.client.engine.curl.*

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = Curl

actual fun terminalManager(terminal: Terminal): TerminalManager = MacosArm64TerminalManager(terminal)