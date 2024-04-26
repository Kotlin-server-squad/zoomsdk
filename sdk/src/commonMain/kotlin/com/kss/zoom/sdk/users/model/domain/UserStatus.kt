package com.kss.zoom.sdk.users.model.domain

enum class UserStatus(val value: String) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    PENDING("pending");

    companion object {
        fun fromString(value: String): UserStatus {
            return entries.find { it.value == value } ?: throw IllegalArgumentException("Unknown value: $value")
        }
    }
}