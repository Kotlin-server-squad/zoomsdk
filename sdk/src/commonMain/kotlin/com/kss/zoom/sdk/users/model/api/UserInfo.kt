package com.kss.zoom.sdk.users.model.api

import com.kss.zoom.sdk.users.model.Email
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.kss.zoom.sdk.users.model.domain.UserInfo as DomainModel

@Serializable
data class UserInfo(
    val email: Email? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    val type: Int,
)

fun UserInfo.toDomain(): DomainModel {
    return DomainModel(
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
        displayName = this.displayName,
        type = this.type,
    )
}