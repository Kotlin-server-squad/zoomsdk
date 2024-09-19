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
    } catch (e: Exception) {
        CallResult.Error.Other(e.message ?: "Unknown error")
    }
}

suspend fun <T> tryCall(onError: suspend (Exception) -> T, block: suspend () -> T): T {
    return try {
        block()
    } catch (e: CancellationException) {
        // Respect cancellation
        throw e
    } catch (e: Exception) {
        onError(e)
    }
}

expect fun currentTimeMillis(): Long

expect fun getProperty(name: String): String?
