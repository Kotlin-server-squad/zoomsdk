package com.kss.zoom.cli

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

fun startServer() = embeddedServer(Netty, port = 8080) {
    routing {
        post("/webhook") {
            // handle the incoming webhook from Zoom
            val call = call
            println("Received a webhook!")
            // You might want to process the call and extract needed information.
            call.respond(HttpStatusCode.OK, "Webhook received")
        }
    }
}.start(wait = false)

fun runShell() {
    while (true) {
        com.kss.zoom.cli.welcomeBanner()
        print("zoom-cli> ")
        val input = readlnOrNull() ?: break // Exit on EOF
        if (input.trim().equals("exit", ignoreCase = true)) break

        val args = input.split(" ").toTypedArray()
        com.kss.zoom.cli.handleCommand(args)
    }
}

fun welcomeBanner() {
    println("𝖹𝗈𝗈𝗆 𝖢𝖫𝖨 𝗏𝟢.𝟣")
    println()
}

fun handleCommand(args: Array<String>) {
    when (args[0]) {
        "help" -> {
            println("Available commands:")
            println("  help - Show this help message")
            println("  login - Login to Zoom")
            println("  exit - Exit the application")
        }

        "login" -> {
            println("Logging in to Zoom...")
            // Perform the login operation
        }

        else -> {
            println("Unknown command. Type 'help' to see available commands.")
        }
    }
}

fun main() {
    val server = com.kss.zoom.cli.startServer()
    try {
        // Make sure the JVM does not exit
        runBlocking {
            com.kss.zoom.cli.runShell()
        }

    } finally {
        server.stop(1000, 10000) // graceful shutdown with timeouts
        println("Server stopped. Exiting...")
    }

}