package com.kss.zoom.module.users.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val action: String,
    @SerialName("user_info")
    val userInfo: UserInfo,
)
