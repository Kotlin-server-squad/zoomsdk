package com.kss.zoom.cli

import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import com.kss.zoom.cli.subcommands.AuthCommand
import com.kss.zoom.sdk.common.call
import io.ktor.client.engine.*
import io.ktor.client.engine.js.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlin.js.Promise

actual fun startServer(zoom: Zoom, terminal: Terminal, commands: List<AuthCommand>) {
    throw UnsupportedOperationException("Not supported on JS")
}

actual fun getSystemProperty(name: String): String? {
    // TODO: Implement
    return null
}

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = Js

actual fun terminalManager(terminal: Terminal): TerminalManager = JsTerminalManager(terminal)

actual fun <T> await(block: suspend () -> Result<T>, onComplete: (T) -> Unit) {
    val scope = MainScope()
    try {
        val runner = AsyncRunner(scope, block)
        runner.await().then { result ->
            onComplete(result)
        }

        // Awaiting the completion of the runner
        var ticker = 0
        ticker = setInterval({
            if (runner.isComplete.value) {
                clearInterval(ticker)
            }
        }, 100)
    } finally {
        scope.cancel()
    }
}

external fun setInterval(handler: () -> Unit, timeout: Int): Int

external fun clearInterval(handle: Int)

private class AsyncRunner<T>(private val scope: CoroutineScope, private val block: suspend () -> Result<T>) {
    val isComplete = atomic(false)

    fun await(): Promise<T> = scope.promise {
        val result = call { block() }
        isComplete.value = true
        result
    }
}
