package com.kss.zoom.sdk.users.model

typealias Email = String
typealias UserId = String

data class User(
    val id: UserId,
    val email: Email,
    val firstName: String,
    val lastName: String,
    val userType: UserType
)