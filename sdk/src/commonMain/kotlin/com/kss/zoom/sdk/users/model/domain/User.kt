package com.kss.zoom.sdk.users.model.domain

import com.kss.zoom.sdk.users.model.Email
import com.kss.zoom.sdk.users.model.UserId

data class User(
    val id: UserId,
    val email: Email,
    val firstName: String,
    val lastName: String,
    val userType: UserType
)