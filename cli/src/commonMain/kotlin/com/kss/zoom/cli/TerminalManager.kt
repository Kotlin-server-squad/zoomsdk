package com.kss.zoom.cli

interface TerminalManager {
    fun enableRawMode()
    fun captureInput(): String
    fun addInputListener(listener: (Char) -> Unit)
}