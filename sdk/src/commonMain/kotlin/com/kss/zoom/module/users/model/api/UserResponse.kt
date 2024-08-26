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
    val email: String,
    val type: Int,
)

fun UserResponse.toModel(): User = TODO()
