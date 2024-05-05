package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ZoomCli {

    private val terminal = Terminal()

    private val command: ZoomCommand

    private val shell: ZoomShell

    private val server: AuthServer

    init {
        val clientId = getSystemProperty("ZOOM_CLIENT_ID") ?: error("ZOOM_CLIENT_ID is not set")
        val clientSecret = getSystemProperty("ZOOM_CLIENT_SECRET") ?: error("ZOOM_CLIENT_SECRET is not set")

        // Zoom SDK
        val zoom = zoom(clientId, clientSecret)

        // Zoom CLI command utilizing the Zoom SDK
        command = ZoomCommand(zoom, terminal)

        shell = ZoomShell(command, terminal)

        // Start the auth server in the background. Use Zoom SDK for OAuth2
        server = AuthServer(zoom, terminal, command.authCommands())
    }

    fun start() {
        shell.start()
    }

    private fun zoom(clientId: String, clientSecret: String): Zoom {
        return Zoom.create(
            clientId, clientSecret,
            httpClient = HttpClient(httpClientEngineFactory()) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                    })
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.NONE
                }
            }
        )
    }
}

expect fun getSystemProperty(name: String): String?

expect fun httpClientEngineFactory(): HttpClientEngineFactory<*>

expect fun terminalManager(terminal: Terminal): TerminalManager