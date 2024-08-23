package com.kss.zoom.common

import com.kss.zoom.model.CallResult
import io.ktor.utils.io.*

suspend fun <T> call(block: suspend () -> CallResult<T>): T {
    return when (val result = tryCall(block)) {
        is CallResult.Success -> result.data
        is CallResult.NotFound -> throw IllegalStateException("Not found")
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
        CallResult.Error(t.message ?: "Unknown error")
    }
}

fun getPropertyOrThrow(name: String): String {
    return getProperty(name) ?: throw IllegalStateException("Property $name not set")
}

expect fun currentTimeMillis(): Long

expect fun getProperty(name: String): String?
