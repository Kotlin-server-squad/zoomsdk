package com.kss.zoom.module.users.model

import com.kss.zoom.model.request.UserRequest
import com.kss.zoom.module.users.model.api.CreateUserRequest
import com.kss.zoom.module.users.model.api.UserInfo

data class CreateRequest(
    override val userId: String,    // Identifies the user making the request.
    val email: String,
    val firstName: String,
    val lastName: String,
    val displayName: String,
) : UserRequest

fun CreateRequest.toApi(): CreateUserRequest =
    CreateUserRequest(
        action = "create",
        userInfo = UserInfo(
            email = email,
            firstName = firstName,
            lastName = lastName,
            displayName = displayName,
            type = 1    // Basic
        )
    )
