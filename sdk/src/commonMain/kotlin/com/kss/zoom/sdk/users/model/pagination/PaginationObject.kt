package com.kss.zoom.sdk.users.model.pagination

import com.kss.zoom.sdk.common.model.Page
import com.kss.zoom.sdk.users.model.api.GetListUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO make it somehow generic - probably via an interface. The problem is that Zoom duplicates this in their responses (there's no generic 'items' field for example)
@Serializable
data class PaginationObject(
    @SerialName("page_count") val pageCount: Int,
    @SerialName("page_number") val pageNumber: Int,
    @SerialName("page_size") val pageSize: Int,
    @SerialName("total_records") val totalRecords: Int,
    val users: List<GetListUser>,
    @SerialName("next_page_token") val nextPageToken: String? = null
)

fun PaginationObject.toDomain(): Page<GetListUser> =
    Page(
        items = this.users,
        pageNumber = pageNumber,
        pageCount = pageCount,
        pageSize = pageSize,
        totalRecords = totalRecords,
        nextPageToken = nextPageToken
    )