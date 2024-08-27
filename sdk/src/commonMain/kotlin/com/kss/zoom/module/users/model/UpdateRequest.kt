package com.kss.zoom.module.users.model

import com.kss.zoom.model.request.UserRequest
import com.kss.zoom.module.users.model.api.UpdateUserRequest

data class UpdateRequest(
    override val userId: String,
    val company: String? = null,
    val department: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val jobTitle: String? = null,
    val language: String? = null,
    val phoneNumbers: List<String> = emptyList(),
    val personalMeetingId: Long? = null,
) : UserRequest

fun UpdateRequest.toApi(): UpdateUserRequest =
    UpdateUserRequest(
        company = this.company,
        department = this.department,
        firstName = this.firstName,
        lastName = this.lastName,
        jobTitle = this.jobTitle,
        language = this.language,
        phoneNumbers = this.phoneNumbers,
        personalMeetingId = this.personalMeetingId
    )
