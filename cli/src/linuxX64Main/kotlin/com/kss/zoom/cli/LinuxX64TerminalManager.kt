package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.set
import platform.posix.*

class LinuxX64TerminalManager(terminal: Terminal) : NativeTerminalManager(terminal) {
    override fun enableRawMode() {
        memScoped {
            val term = alloc<termios>()
            tcgetattr(STDIN_FILENO, term.ptr)

            term.c_lflag = term.c_lflag and ICANON.inv().toUInt() // disable canonical mode and echo
            term.c_cc[VMIN] = 1u // minimum number of characters for noncanonical read
            term.c_cc[VTIME] = 0u // timeout for noncanonical read
            tcsetattr(STDIN_FILENO, TCSANOW, term.ptr)
        }
    }
}