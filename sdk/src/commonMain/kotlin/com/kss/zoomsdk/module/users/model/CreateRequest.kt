package com.kss.zoomsdk.module.users.model

data class CreateRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val displayName: String,
)
