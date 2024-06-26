package com.kss.zoom.sdk.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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

actual suspend fun <T> withMDCContext(mdcData: Map<String, String>, block: suspend () -> T): T {
    return withContext(MDCContext(mdcData)) {
        block()
    }
}