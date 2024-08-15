package com.kss.zoom.common

import com.kss.zoom.model.CallResult
import kotlinx.datetime.Instant

suspend fun <T> call(block: suspend () -> CallResult<T>): T {
    return when (val result = block()) {
        is CallResult.Success -> result.data
        is CallResult.NotFound -> throw IllegalStateException("Not found")
        is CallResult.Error -> throw IllegalStateException("Call failed: ${result.message}")
    }
}

fun String.toTimestamp(): Long {
    return Instant.parse(this).toEpochMilliseconds()
}

expect fun currentTimeMillis(): Long
