package com.kss.zoom.module.users.model.api

import com.kss.zoom.module.users.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val id: String,
    val type: Int,
    val email: String? = null,
    val company: String? = null,
)

fun UserResponse.toModel(): User =
    User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        displayName = "$firstName $lastName",
        type = type,
        company = company,
    )
