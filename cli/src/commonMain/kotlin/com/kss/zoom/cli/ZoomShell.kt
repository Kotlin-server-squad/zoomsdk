package com.kss.zoom.cli

import com.github.ajalt.clikt.core.NoSuchOption
import com.github.ajalt.clikt.core.NoSuchSubcommand
import com.github.ajalt.mordant.terminal.Terminal

class ZoomShell(private val command: ZoomCommand, private val terminal: Terminal) {

    companion object {
        fun prompt(): String {
            return "zoom-cli> "
        }
    }
    private val terminalManager = terminalManager(terminal)

    fun start() {
        showWelcomeBanner()
        terminalManager.enableRawMode()

        while (true) {
            showPrompt()
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
            terminal.print(prompt() + suggestion)
            val capturedInput = terminalManager.captureInput()
            if (capturedInput.trim().isEmpty()) suggestion else capturedInput
        } else {
            terminal.print(prompt() + matches.joinToString(" "))
            terminalManager.addInputListener {
                terminal.cursor.move {
                    clearLine()
                    startOfLine()
                }
                terminal.print(prompt() + it)
            }
            suggestCompletion(terminalManager.captureInput().trim(), command)
        }
    }

    private fun showWelcomeBanner() {
        println("Zoom CLI v0.1")
    }

    private fun showPrompt() {
        print(prompt())
    }
}