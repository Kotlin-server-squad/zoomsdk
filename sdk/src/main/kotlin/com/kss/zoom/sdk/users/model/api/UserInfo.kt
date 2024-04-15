package com.kss.zoom.sdk.users.model.api

import com.kss.zoom.sdk.users.model.Email
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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


