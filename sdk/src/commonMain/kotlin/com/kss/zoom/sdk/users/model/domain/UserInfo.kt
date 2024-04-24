package com.kss.zoom.sdk.users.model.domain

data class UserInfo(
    val id: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val type: Int,
    val personalMeetingId: Long? = null,
    val roleName: String? = null,
    val roleId: String? = null,
    val displayName: String? = null,
    val accountId: String? = null,
    val accountNumber: Int? = null,
    val status: UserStatus? = null,
    val phoneNumbers: List<PhoneNumber> = emptyList(),
    val company: String? = null,
    val jobTitle: String? = null,
    val department: String? = null,
)
