package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal

class JvmTerminalManager(private val terminal: Terminal) : TerminalManager {
    override fun enableRawMode() {
        TODO("Not yet implemented")
    }

    override fun captureInput(): String {
        TODO("Not yet implemented")
    }

    override fun addInputListener(listener: (Char) -> Unit) {
        TODO("Not yet implemented")
    }
}