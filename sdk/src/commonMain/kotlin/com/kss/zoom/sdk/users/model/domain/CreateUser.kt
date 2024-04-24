package com.kss.zoom.sdk.users.model.domain

import com.kss.zoom.sdk.users.model.Email

data class CreateUser(
    val email: Email,
    val firstName: String,
    val lastName: String,
    val displayName: String? = null,
    val type: UserType,
    val action: Action,
)