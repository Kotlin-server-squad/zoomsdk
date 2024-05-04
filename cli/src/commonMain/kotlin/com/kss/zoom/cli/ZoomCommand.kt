package com.kss.zoom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context

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
        context {
            helpOptionNames = emptySet()
        }
//        completionOption()
    }
    override fun run() {
    }
}