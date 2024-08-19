package com.kss.zoom.common.extensions

import com.kss.zoom.model.CallResult
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun String.toTimestamp(): Long {
    return Instant.parse(this).toEpochMilliseconds()
}

fun <T, R> CallResult<T>.map(transform: (T) -> R): CallResult<R> = when (this) {
    is CallResult.Success -> CallResult.Success(transform(data))
    is CallResult.Error -> CallResult.Error(message)
    is CallResult.NotFound -> CallResult.NotFound
}

fun Long.toIsoDateTimeString(timezone: String): String {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.of(timezone)).toString()
}
