package com.kss.zoom.model.pagination

import com.kss.zoom.module.meetings.model.api.MeetingResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationObject<T>(
    @SerialName("page_count") val pageCount: Short,
    @SerialName("page_number") val pageNumber: Short,
    @SerialName("page_size") val pageSize: Short,
    @SerialName("total_records") val totalRecords: Int,
    val records: List<T>,
    @SerialName("next_page_token") val nextPageToken: String? = null,
)
