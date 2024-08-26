package com.kss.zoom.module.users.model

import com.kss.zoom.model.request.UserRequest
import com.kss.zoom.module.users.model.api.UpdateUserRequest

data class UpdateRequest(
    override val userId: String,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String? = null,
) : UserRequest

fun UpdateRequest.toApi(): UpdateUserRequest = TODO()
