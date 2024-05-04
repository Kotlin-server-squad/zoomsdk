package com.kss.zoom.cli

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.NoSuchOption
import com.github.ajalt.clikt.core.NoSuchSubcommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import com.kss.zoom.auth.model.AuthorizationCode
import com.kss.zoom.cli.subcommands.*
import com.kss.zoom.sdk.common.call
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private val terminal = Terminal()
private val terminalManager = terminalManager(terminal)
fun runZoomCli(args: Array<String>) {
    val command = zoomCommand()
    if (args.isNotEmpty() && args[0].startsWith("--generate-completion")) {
        command.completionOption().main(args)
    } else {
        var server: ApplicationEngine? = null
        try {
            val zoom = zoom()
            val listMeetingsCommand = ListMeetingsCommand(zoom)
            server = startServer(zoom, listMeetingsCommand)
            server.addShutdownHook {
                server.gracefulStop()
            }
            runShell(command)
        } finally {
            server?.gracefulStop()
        }
    }
}

private fun zoom(): Zoom {
    val clientId = getSystemProperty("ZOOM_CLIENT_ID") ?: error("ZOOM_CLIENT_ID is not set")
    val clientSecret = getSystemProperty("ZOOM_CLIENT_SECRET") ?: error("ZOOM_CLIENT_SECRET is not set")
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

private fun zoomCommand(): ZoomCommand {
    val zoom = zoom()
    val listMeetingsCommand = ListMeetingsCommand(zoom)
    val loginCommand = LoginCommand(zoom, terminal)

    return ZoomCommand().subcommands(
        HelpCommand(terminal),
        VerboseCommand(),
        ColorsCommand(),
        listMeetingsCommand,
        loginCommand
    )
}

private fun ApplicationEngine.gracefulStop() {
    this.stop(1000, 10000)
    terminal.println("Server stopped, exiting...")
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

private fun welcomeBanner() {
    println("Zoom CLI v0.1")
}

private fun runShell(command: ZoomCommand) {
    terminalManager.enableRawMode()
    welcomeBanner()

    while (true) {
        print("zoom-cli> ")
        try {
            val userInput = validateInput(terminalManager.captureInput(), command)
            val trimmedInput = userInput.trim()
            if (trimmedInput.equals("exit", ignoreCase = true)) {
                break
            }
            val args = trimmedInput.split(" ").toTypedArray()
            command.parse(args)
        } catch (e: NoSuchOption) {
            terminal.println("Unknown option", stderr = true)
        } catch (e: NoSuchSubcommand) {
            terminal.println("Unknown command", stderr = true)
        } catch (e: Throwable) {
            terminal.println("Error: ${e.message ?: "unknown error"}", stderr = true)
        }
    }
}

private fun validateInput(input: String, command: ZoomCommand): String {
    return if (input.last() == '\t') {
        validateInput(suggestCompletion(input.trim(), command), command)
    } else {
        input
    }
}
private fun suggestCompletion(input: String, command: ZoomCommand): String {
    val matches = command.registeredSubcommands().map { it.commandName }.filter { it.startsWith(input) }.sorted()
    val suggestion = when {
        matches.isEmpty() -> input
        matches.size == 1 -> matches.first()
        else -> null
    }
    terminal.cursor.move {
        clearLine()
        startOfLine()
    }
    return if (suggestion != null) {
        terminal.print("zoom-cli> $suggestion")
        suggestion + terminalManager.captureInput()
    } else {
        terminal.print("zoom-cli> " + matches.joinToString(" "))
        terminalManager.addInputListener {
            terminal.cursor.move {
                clearLine()
                startOfLine()
            }
            terminal.print("zoom-cli> ${input + it}")
        }
        suggestCompletion(input + terminalManager.captureInput().trim(), command)
    }
}

expect fun getSystemProperty(name: String): String?

expect fun httpClientEngineFactory(): HttpClientEngineFactory<*>

expect fun terminalManager(terminal: Terminal): TerminalManager