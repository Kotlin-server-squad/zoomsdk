package com.kss.zoom.test

import kotlinx.datetime.*
import kotlin.time.Duration

class TestClock(private val timezone: TimeZone = TimeZone.UTC) : Clock {
    private val instant = LocalDateTime(2024, 1, 1, 0, 0, 0).toInstant(timezone)

    override fun now(): Instant = instant

    fun plus(duration: Duration): LocalDateTime =
        instant.plus(duration).toLocalDateTime(timezone)

    fun minus(duration: Duration): LocalDateTime =
        instant.minus(duration).toLocalDateTime(timezone)

    fun toIsoString(dateTime: LocalDateTime): String =
        dateTime.toInstant(timezone).toString()
}
