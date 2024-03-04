package com.kss.zoom.sdk.model.domain.users

data class CreateUser(
    val email: Email,
    val firstName: String,
    val lastName: String,
    val displayName: String? = null
)