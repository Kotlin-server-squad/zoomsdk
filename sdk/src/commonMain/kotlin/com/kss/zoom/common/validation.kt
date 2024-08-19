package com.kss.zoom.common

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

fun String.notBlank(fieldName: String) {
    require(this.isNotBlank()) { "$fieldName must not be blank" }
}

fun Short.greaterZero(fieldName: String) {
    require(this > 0) { "$fieldName must be greater than 0" }
}

fun LocalDateTime.isInFuture(fieldName: String, clock: Clock) {
    require(this.toInstant(TimeZone.UTC) > clock.now()) { "$fieldName must be in the future" }
}
