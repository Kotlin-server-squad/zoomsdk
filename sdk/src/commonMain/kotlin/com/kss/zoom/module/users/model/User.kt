package com.kss.zoom.module.users.model

data class User(
    val id: String,
    val email: String?,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val type: Int,
    val company: String? = null,
//    val createdAt: Long,
//    val lastLoginAt: Long,
//    val lastActivityAt: Long,
//    val status: String
)
