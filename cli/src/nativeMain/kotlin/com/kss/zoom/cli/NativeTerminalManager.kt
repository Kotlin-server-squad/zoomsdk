package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.cinterop.refTo
import platform.posix.STDIN_FILENO
import platform.posix.read

abstract class NativeTerminalManager(private val terminal: Terminal) : TerminalManager {

    private val inputListeners = mutableListOf<(Char) -> Unit>()

    override fun captureInput(): String {
        val buffer = ByteArray(1)
        val inputBuffer = StringBuilder()
        loop@ while (true) {
            when (read(STDIN_FILENO, buffer.refTo(0), 1u).toInt()) {
                1 -> {
                    when (val char = buffer[0].toInt().toChar()) {
                        '\n', '\r', '\t' -> {
                            inputBuffer.append(char) // Capture the ending character
                            break@loop
                        }

                        '\u007F' -> {   // Backspace
                            inputBuffer.takeIf { it.isNotEmpty() }?.deleteAt(inputBuffer.length - 1)
                            terminal.cursor.move {
                                if (inputBuffer.isEmpty()) {
                                    clearLine()
                                    startOfLine()
                                } else {
                                    left(3)
                                    clearLineAfterCursor()
                                }
                            }
                            if (inputBuffer.isEmpty()) {
                                terminal.print(ZoomShell.prompt())
                            }
                        }

                        else -> {
                            inputBuffer.append(char)
                            inputListeners.forEach { it(char) }
                            inputListeners.clear()
                        }
                    }
                }

                else -> {
                    inputBuffer.clear()
                    break
                }
            }
        }
        return inputBuffer.toString()
    }

    override fun addInputListener(listener: (Char) -> Unit) {
        inputListeners.add(listener)
    }
}