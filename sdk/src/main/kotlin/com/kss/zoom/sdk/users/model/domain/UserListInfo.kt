package com.kss.zoom.sdk.users.model.domain

data class UserListInfo(
    val department: String? = null,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val id: String? = null,
    val personalMeetingId: Long? = null,
    val roleId: String? = null,
    val type: Int,
    val verifed: Int? = null,
    val displayName: String? = null,
    val status: Status,
)
