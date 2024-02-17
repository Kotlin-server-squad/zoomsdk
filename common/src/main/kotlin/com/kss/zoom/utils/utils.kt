package com.kss.zoom.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

suspend fun <T> call(block: suspend () -> Result<T>): T =
    block().getOrThrow()

fun <T> callSync(block: suspend () -> Result<T>): T = runBlocking {
    block().getOrThrow()
}

fun <T> future(block: suspend () -> Result<T>): CompletableFuture<T> {
    val scope = CoroutineScope(Dispatchers.IO)
    return scope.future { block().getOrThrow() }
}
