package com.kss.zoom.common

fun String.notBlank(fieldName: String) {
    require(this.isNotBlank()) { "$fieldName must not be blank" }
}

fun Long.greaterZero(fieldName: String) {
    require(this > 0) { "$fieldName must be greater than 0" }
}

fun Short.greaterZero(fieldName: String) {
    require(this > 0) { "$fieldName must be greater than 0" }
}
