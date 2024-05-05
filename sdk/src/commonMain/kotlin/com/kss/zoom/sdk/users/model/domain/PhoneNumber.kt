package com.kss.zoom.sdk.users.model.domain

import kotlinx.serialization.Serializable
import com.kss.zoom.sdk.users.model.api.UpdateUserRequest.PhoneNumber as ApiPhoneNumber

@Serializable
data class PhoneNumber(
    val code: String? = null,
    //TODO implement: https://developers.zoom.us/docs/api/rest/other-references/abbreviation-lists/#countries
    val country: String? = null,
    val label: Label? = null,
    val number: String?= null,
    val verified: Boolean? = null,
)

fun PhoneNumber.toApi(): ApiPhoneNumber {
    return ApiPhoneNumber(
        code = this.code,
        country = this.country,
        label = this.label?.value,
        number = this.number,
    )
}