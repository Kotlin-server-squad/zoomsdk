package com.kss.zoom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.kss.zoom.Zoom
import com.kss.zoom.auth.model.AuthorizationCode
import com.kss.zoom.cli.subcommands.*
import com.kss.zoom.sdk.common.call
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

fun runZoomCli() {
    val clientId = getSystemProperty("ZOOM_CLIENT_ID") ?: error("ZOOM_CLIENT_ID is not set")
    val clientSecret = getSystemProperty("ZOOM_CLIENT_SECRET") ?: error("ZOOM_CLIENT_SECRET is not set")
    val zoom = Zoom.create(clientId, clientSecret)
    val listMeetingsCommand = ListMeetingsCommand(zoom)
    val server = startServer(zoom, listMeetingsCommand)
    try {
        runShell(listMeetingsCommand, LoginCommand(zoom))
    } finally {
        server.stop(1000, 10000)
        println("Server stopped, exiting...")
    }
}

private fun startServer(zoom: Zoom, vararg authCommands: AuthCommand) = embeddedServer(CIO, port = 8080) {
    routing {
        get("/callback") {
            val code = call.request.queryParameters["code"]
            if (code != null) {
                launch {
                    try {
                        val tokens = call { zoom.auth().authorizeUser(AuthorizationCode(code)) }
                        authCommands.forEach { it.setUserTokens(tokens) }
                    } catch (e: Exception) {
                        println("Error handling callback: $e")
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

private fun welcomeBanner() {
    println("Zoom CLI v0.1")
}

private fun runShell(vararg customCommands: CliktCommand) {
    val command = ZoomCommand()
        .subcommands(
            HelpCommand(),
            VerboseCommand(),
            ColorsCommand(),
            *customCommands
        )
    while (true) {
        welcomeBanner()
        print("zoom-cli> ")
        val input = readlnOrNull() ?: break // Exit on EOF
        if (input.trim().equals("exit", ignoreCase = true)) break
        val args = input.split(" ").toTypedArray()
        command.main(args)
    }
}

expect fun getSystemProperty(name: String): String?