package com.kss.zoom.sdk.users.model.domain

import com.kss.zoom.sdk.users.model.api.UpdateUserRequest

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


fun UpdateUser.toApi(): UpdateUserRequest  {
    return UpdateUserRequest(
        company = this.company,
        department = this.department,
        firstName = this.firstName,
        lastName = this.lastName,
        jobTitle = this.jobTitle,
        language = this.language,
        phoneNumbers = this.phoneNumbers.map { it.toApi() },
        personalMeetingId = this.personalMeetingId
    )
}