package com.kss.zoom.module.users.model

import com.kss.zoom.model.request.UserRequest

data class GetRequest(
    override val userId: String,
) : UserRequest
