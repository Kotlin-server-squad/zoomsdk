package com.kss.zoom.cli.subcommands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal

class ColorsCommand : CliktCommand(help = "List available colors", name = "colors") {
    override fun run() {
        val t = Terminal()
        t.println("${TextColors.red("red")} ${TextColors.white("white")} and ${TextColors.blue("blue")}")
    }
}