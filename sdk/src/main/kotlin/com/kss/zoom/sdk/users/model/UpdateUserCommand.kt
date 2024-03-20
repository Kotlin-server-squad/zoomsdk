package com.kss.zoom.sdk.users.model

data class UpdateUserCommand(
    val id: UserId,
    val email: Email? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String? = null
)