package com.kss.zoom.cli

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal

class ZoomCommand : CliktCommand(
    help = """
        CLI for Zoom API
    """.trimIndent(),
    epilog = """
        This CLI allows you to interact with the Zoom API.
    """.trimIndent(),
    name = "zoomcli"
) {
    init {
        completionOption()
    }
    override fun run() {
    }
}