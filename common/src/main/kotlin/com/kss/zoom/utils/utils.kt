package com.kss.zoom.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

suspend fun <T> call(block: suspend () -> Result<T>): T =
    block().getOrThrow()

fun <T> callSync(block: suspend () -> Result<T>): T {
    val executor = Executors.newSingleThreadExecutor()
    val latch = CountDownLatch(1)
    var result: Result<T>? = null
    executor.execute {
        runBlocking {
            result = block()
            latch.countDown()
        }
    }
    latch.await()
    executor.shutdown()
    return result!!.getOrThrow()
}

fun <T> future(block: suspend () -> Result<T>): CompletableFuture<T> {
    val scope = CoroutineScope(Dispatchers.IO)
    return scope.future { block().getOrThrow() }
}
