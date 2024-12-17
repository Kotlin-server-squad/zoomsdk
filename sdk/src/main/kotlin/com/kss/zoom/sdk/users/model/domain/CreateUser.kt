package com.kss.zoom.sdk.users.model.domain

import com.kss.zoom.sdk.users.model.Email
import com.kss.zoom.sdk.users.model.api.CreateUserRequest
import com.kss.zoom.sdk.users.model.api.UserInfo

data class CreateUser(
    val email: Email,
    val firstName: String,
    val lastName: String,
    val displayName: String? = null,
    val type: Type,
    val action: Action,
)

fun CreateUser.toApi(): CreateUserRequest {
    return CreateUserRequest(
        action = this.action.value, userInfo = UserInfo(
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            displayName = this.displayName,
            type = this.type.value
        )
    )
}