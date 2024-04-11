package com.kss.zoom.cli.subcommands

import com.github.ajalt.clikt.core.CliktCommand

class VerboseCommand : CliktCommand(help = "Enable verbose logging", name = "verbose") {
    override fun run() {
        println("Verbose logging enabled")
    }
}