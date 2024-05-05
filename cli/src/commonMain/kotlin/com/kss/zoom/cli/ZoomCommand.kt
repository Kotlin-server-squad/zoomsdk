package com.kss.zoom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import com.kss.zoom.cli.subcommands.*

class ZoomCommand(zoom: Zoom, terminal: Terminal) : CliktCommand(
    help = """
        CLI for Zoom API
    """.trimIndent(),
    epilog = """
        This CLI allows you to interact with the Zoom API.
    """.trimIndent(),
    name = "zoomcli"
) {
    init {
        context {
            helpOptionNames = emptySet()
        }

        subcommands(
            HelpCommand(terminal),
            VerboseCommand(),
            ColorsCommand(),
            ListMeetingsCommand(zoom),
            LoginCommand(zoom, terminal),
            MeCommand(zoom)
        )
    }

    fun authCommands(): List<AuthCommand> =
        registeredSubcommands().filterIsInstance<AuthCommand>()

    override fun run() {
    }
}