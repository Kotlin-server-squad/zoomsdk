package com.kss.zoom.module.users.model

import com.kss.zoom.model.request.UserRequest
import com.kss.zoom.module.users.model.api.CreateUserRequest

data class CreateRequest(
    override val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val displayName: String,
) : UserRequest

fun CreateRequest.toApi(): CreateUserRequest = TODO()
