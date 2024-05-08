package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import com.kss.zoom.cli.subcommands.AuthCommand
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ZoomCli {

    fun start(withServer: Boolean = true) {
        val clientId = getSystemProperty("ZOOM_CLIENT_ID") ?: error("ZOOM_CLIENT_ID is not set")
        val clientSecret = getSystemProperty("ZOOM_CLIENT_SECRET") ?: error("ZOOM_CLIENT_SECRET is not set")

        // Zoom SDK
        val zoom = zoom(clientId, clientSecret)

        val terminal = Terminal()

        // Zoom CLI command utilizing the Zoom SDK
        val command = ZoomCommand(zoom, terminal)

        if (withServer) {
            // Start the auth server in the background. Use Zoom SDK for OAuth2
            startServer(zoom, terminal, command.authCommands())
        } else {
            // Run the CLI without the server.
            // This is useful for running in environments where a server cannot be started, such as in Node.js on in the browser.
        }

        ZoomShell(command, terminal).start()
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

expect fun startServer(zoom: Zoom, terminal: Terminal, commands: List<AuthCommand>)

expect fun getSystemProperty(name: String): String?

expect fun httpClientEngineFactory(): HttpClientEngineFactory<*>

expect fun terminalManager(terminal: Terminal): TerminalManager

expect fun <T> await(block: suspend () -> Result<T>, onComplete: (T) -> Unit)