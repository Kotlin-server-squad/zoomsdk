package com.kss.zoom.cli.subcommands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.terminal.Terminal

class HelpCommand(private val terminal: Terminal) : CliktCommand(help = "Help for Zoom CLI", name = "help") {

    override fun run() {
        terminal.println("Usage: zoomcli [COMMAND]")
    }
}