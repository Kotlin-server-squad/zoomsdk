package com.kss.zoom.sdk.model

data class User(
    val id: UserId,
    val email: Email,
    val firstName: String,
    val lastName: String,
    val userType: UserType
)

enum class UserType {
    Basic,
    Licensed,
    None
}

data class CreateUser(
    val email: Email,
    val firstName: String,
    val lastName: String,
    val displayName: String? = null
)

data class UpdateUser(
    val email: Email? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String? = null
)

typealias Email = String
typealias UserId = String
