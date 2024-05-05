package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.cinterop.*
import platform.posix.*

class NativeTerminalManager(private val terminal: Terminal) : TerminalManager {

    private val inputListeners = mutableListOf<(Char) -> Unit>()

    override fun enableRawMode() {
        memScoped {
            val term = alloc<termios>()
            tcgetattr(STDIN_FILENO, term.ptr)

            term.c_lflag = term.c_lflag and ICANON.inv().toULong() // disable canonical mode and echo
            term.c_cc[VMIN] = 1u // minimum number of characters for noncanonical read
            term.c_cc[VTIME] = 0u // timeout for noncanonical read
            tcsetattr(STDIN_FILENO, TCSANOW, term.ptr)
        }
    }

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