package com.kss.zoom.sdk.users.model.api.pagination

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPageQuery(
    val status: String? = null,
    @SerialName("page_size")
    val pageSize: Int?=null,
    @SerialName("role_id")
    val roleId: Int?= null,
    @SerialName("page_number")
    val pageNumber: Int?=null,
    @SerialName("next_page_token")
    val nextPageToken: String?=null,
)
