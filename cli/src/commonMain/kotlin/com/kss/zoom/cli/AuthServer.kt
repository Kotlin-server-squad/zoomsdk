package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import com.kss.zoom.auth.model.AuthorizationCode
import com.kss.zoom.cli.subcommands.AuthCommand
import com.kss.zoom.sdk.common.call
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

class AuthServer(zoom: Zoom, terminal: Terminal, commands: List<AuthCommand>) {
    init {
        val engine = embeddedServer(CIO, port = 8080) {
            routing {
                get("/callback") {
                    val code = call.request.queryParameters["code"]
                    if (code != null) {
                        launch {
                            try {
                                val tokens = call { zoom.auth().authorizeUser(AuthorizationCode(code)) }
                                commands.forEach { it.setUserTokens(tokens) }
                            } catch (e: Exception) {
                                terminal.println("Error handling callback: ${e.message ?: "unknown error"}", stderr = true)
                            }
                        }
                    }
                    call.respond(
                        HttpStatusCode.OK,
                        "Authorization successful! You can close this window now and return to the terminal."
                    )
                }
            }
        }.start(wait = false)
        engine.addShutdownHook {
            engine.stop(1000, 10000)
            terminal.println("Server stopped, exiting...")
        }
    }
}

