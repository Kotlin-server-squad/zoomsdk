package com.kss.zoom.sdk.model.domain.users

data class UpdateUser(
    val email: Email? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String? = null
)
typealias Email = String
typealias UserId = String