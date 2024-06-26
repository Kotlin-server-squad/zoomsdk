package com.kss.zoom.sdk.users.model.domain

enum class UserType(val value: Int) {
    BASIC(1),
    LICENSED(2),
    NONE(99), // None (this can only be set with ssoCreate)
    ;
    companion object {
        fun fromInt(value: Int): UserType {
            return entries.find { it.value == value } ?: throw IllegalArgumentException("Unknown value: $value")
        }
    }
}