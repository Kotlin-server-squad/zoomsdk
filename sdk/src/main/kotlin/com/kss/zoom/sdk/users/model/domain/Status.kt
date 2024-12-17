package com.kss.zoom.sdk.users.model.domain

enum class Status(val value: String) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    PENDING("pending");

    companion object {
        fun fromString(value: String): Status {
            return values().find { it.value == value } ?: throw IllegalArgumentException("Unknown value: $value")
        }
    }
}