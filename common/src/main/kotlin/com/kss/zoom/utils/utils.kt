package com.kss.zoom.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

suspend fun <T> call(block: suspend () -> Result<T>): T =
    block().getOrThrow()

fun <T> callSync(executor: ExecutorService = Executors.newSingleThreadExecutor(), block: suspend () -> Result<T>): T {
    return runBlocking(executor.asCoroutineDispatcher()) {
        block().getOrThrow()
    }
}

fun <T> callAsync(executor: ExecutorService = Executors.newSingleThreadExecutor(), block: suspend () -> Result<T>): CompletableFuture<T> {
    return CoroutineScope(executor.asCoroutineDispatcher()).future {
        block().getOrThrow()
    }
}
