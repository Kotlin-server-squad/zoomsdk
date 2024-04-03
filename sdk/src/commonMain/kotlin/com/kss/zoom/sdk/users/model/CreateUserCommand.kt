package com.kss.zoom.sdk.users.model

data class CreateUserCommand(
    val email: Email,
    val firstName: String,
    val lastName: String,
    val displayName: String? = null
)