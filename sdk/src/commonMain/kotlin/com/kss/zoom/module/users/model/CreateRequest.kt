package com.kss.zoom.module.users.model

data class CreateRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val displayName: String,
)
