package com.kss.zoom.cli.subcommands

import com.github.ajalt.clikt.core.CliktCommand

class HelpCommand : CliktCommand(help = "Help for Zoom CLI", name = "help") {
    override fun run() {
        echo("This is the help for the Zoom CLI")
    }
}