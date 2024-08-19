package com.kss.zoom.module.meetings.model.api

import com.kss.zoom.model.pagination.Page
import com.kss.zoom.module.meetings.model.Meeting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationObject(
    @SerialName("page_count") val pageCount: Short,
    @SerialName("page_number") val pageNumber: Short,
    @SerialName("page_size") val pageSize: Short,
    @SerialName("total_records") val totalRecords: Int,
    val meetings: List<MeetingResponse>,
    @SerialName("next_page_token") val nextPageToken: String? = null,
)

fun PaginationObject.toModel(): Page<Meeting> =
    Page(
        index = pageNumber,
        size = pageSize,
        items = meetings.map { it.toModel() },
        nextPageToken = nextPageToken
    )
