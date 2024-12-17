package com.kss.zoom.sdk.users.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetListUser(
    @SerialName("dept")
    val department: String? = null,
    val email: String,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val id: String? = null,
    @SerialName("pmi")
    val personalMeetingId: Long? = null,
    @SerialName("role_id")
    val roleId: String? = null,
    val type: Int,
    val verifed: Int? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    val status: String,
)