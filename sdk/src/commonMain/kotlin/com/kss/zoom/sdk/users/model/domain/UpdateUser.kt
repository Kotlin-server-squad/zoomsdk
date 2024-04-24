package com.kss.zoom.sdk.users.model.domain

data class UpdateUser(
    val company: String? = null,
    val department: String?=null,
    val firstName: String? = null,
    val lastName: String? = null,
    val jobTitle: String? = null,
    val language: String? = null,
    val phoneNumbers: List<PhoneNumber> = emptyList(),
    val personalMeetingId: Long? = null,
)
