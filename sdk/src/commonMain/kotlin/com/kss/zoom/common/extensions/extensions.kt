package com.kss.zoom.common.extensions

import com.kss.zoom.model.CallResult
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun String.toTimestamp(): Long {
    return Instant.parse(this).toEpochMilliseconds()
}

fun <T, R> CallResult<T>.map(transform: (T) -> R): CallResult<R> = when (this) {
    is CallResult.Success -> CallResult.Success(transform(data))
    is CallResult.Error -> this
}

fun Long.toIsoDateTimeString(timezone: String): String {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.of(timezone)).toString()
}

fun LocalDateTime.toDateTimeString(): String {
    return "${date.year}-${date.monthNumber}-${date.dayOfMonth}T${time.hour}:${time.minute}:${time.second}"
}

fun TimeZone.id(): String = when (this) {
    TimeZone.UTC -> "UTC"
    else -> id
}
