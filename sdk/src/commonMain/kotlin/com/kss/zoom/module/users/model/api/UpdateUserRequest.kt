package com.kss.zoom.module.users.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val company: String?,
    @SerialName("dept")
    val department: String?,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("last_name")
    val lastName: String?,
    @SerialName("job_title")
    val jobTitle: String?,
    @SerialName("language")
    val language: String?,
    @SerialName("phone_numbers")
    val phoneNumbers: List<String>,
    @SerialName("pmi")
    val personalMeetingId: Long?,
)
