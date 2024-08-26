package com.kss.zoom.module.users.model

import com.kss.zoom.model.request.UserRequest

data class DeleteRequest(
    override val userId: String,
) : UserRequest
