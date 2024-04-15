package com.kss.zoom.sdk.users.model.api

import com.kss.zoom.sdk.users.model.domain.Label
import com.kss.zoom.sdk.users.model.domain.Status
import com.kss.zoom.sdk.users.model.domain.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.kss.zoom.sdk.users.model.domain.PhoneNumber as ApiPhoneNumber

@Serializable
data class GetUser(
    val id: String? = null,
    val email: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val type: Int,
    @SerialName("pmi")
    val personalMeetingId: Long? = null,
    @SerialName("role_name")
    val roleName: String? = null,
    @SerialName("role_id")
    val roleId: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("account_id")
    val accountId: String? = null,
    @SerialName("account_number")
    val accountNumber: Int? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("phone_numbers")
    val phoneNumbers: List<PhoneNumber> = emptyList(),
    val company: String? = null,
    @SerialName("job_title")
    val jobTitle: String? = null,
    @SerialName("dept")
    val department: String? = null,
) {
    @Serializable
    data class PhoneNumber(
        val code: String? = null,
        //TODO implement: https://developers.zoom.us/docs/api/rest/other-references/abbreviation-lists/#countries
        val country: String? = null,
        val label: String? = null,
        val number: String? = null,
        val verified: Boolean? = null,
    )
}

fun GetUser.toDomain(): UserInfo {
    return UserInfo(
        id = this.id,
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
        type = this.type,
        personalMeetingId = this.personalMeetingId,
        roleName = this.roleName,
        roleId = this.roleId,
        displayName = this.displayName,
        accountId = this.accountId,
        accountNumber = this.accountNumber,
        status = this.status?.let { Status.fromString(it) },
        phoneNumbers = this.phoneNumbers.map { it.toDomain() },
        company = this.company,
        jobTitle = this.jobTitle,
        department = this.department,
    )
}

fun GetUser.PhoneNumber.toDomain(): ApiPhoneNumber {
    return ApiPhoneNumber(
        code = this.code,
        country = this.country,
        label = this.label?.let { Label.fromString(it) },
        number = this.number,
        verified = this.verified
    )
}