package com.kss.zoom.sdk.users.model.api

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
    val phoneNumbers: List<PhoneNumber>,
    @SerialName("pmi")
    val personalMeetingId: Long?,
) {
    @Serializable
    data class PhoneNumber(
        val code: String? = null,
        //TODO implement: https://developers.zoom.us/docs/api/rest/other-references/abbreviation-lists/#countries
        val country: String? = null,
        val label: String? = null,
        val number: String? = null,
    )
}
