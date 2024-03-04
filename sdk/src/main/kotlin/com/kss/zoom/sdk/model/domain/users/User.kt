package com.kss.zoom.sdk.model.domain.users

data class User(
    val id: UserId,
    val email: Email,
    val firstName: String,
    val lastName: String,
    val userType: UserType
)