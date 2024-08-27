package com.kss.zoom.module.users.model.api

import com.kss.zoom.model.pagination.Page
import com.kss.zoom.module.users.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationObject(
    @SerialName("page_count") val pageCount: Short,
    @SerialName("page_number") val pageNumber: Short,
    @SerialName("page_size") val pageSize: Short,
    @SerialName("total_records") val totalRecords: Int,
    val users: List<UserResponse>,
    @SerialName("next_page_token") val nextPageToken: String? = null,
)

fun PaginationObject.toModel(): Page<User> =
    Page(
        index = pageNumber,
        size = pageSize,
        items = users.map { it.toModel() },
        nextPageToken = nextPageToken
    )
