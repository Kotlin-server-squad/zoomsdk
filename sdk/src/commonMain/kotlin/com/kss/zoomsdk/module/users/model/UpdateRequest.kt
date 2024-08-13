package com.kss.zoomsdk.module.users.model

data class UpdateRequest(
    val userId: String,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String? = null,
)
