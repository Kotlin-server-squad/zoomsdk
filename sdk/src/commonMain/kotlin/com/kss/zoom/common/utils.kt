package com.kss.zoom.common

import com.kss.zoom.model.CallResult
import io.ktor.utils.io.*

suspend fun <T> call(block: suspend () -> CallResult<T>): T {
    return when (val result = tryCall(block)) {
        is CallResult.Success -> result.data
        is CallResult.Error -> throw IllegalStateException("Call failed: ${result.message}")
    }
}

suspend fun <T> tryCall(block: suspend () -> CallResult<T>): CallResult<T> {
    return try {
        block()
    } catch (e: CancellationException) {
        // Respect cancellation
        throw e
    } catch (t: Throwable) {
        CallResult.Error.Other(t.message ?: "Unknown error")
    }
}

suspend fun <T> tryCall(onError: suspend (Throwable) -> T, block: suspend () -> T): T {
    return try {
        block()
    } catch (e: CancellationException) {
        // Respect cancellation
        throw e
    } catch (t: Throwable) {
        onError(t)
    }
}

expect fun currentTimeMillis(): Long

expect fun getProperty(name: String): String?