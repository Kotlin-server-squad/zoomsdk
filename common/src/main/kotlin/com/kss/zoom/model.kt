package com.kss.zoom

import java.time.LocalDateTime

class ZoomException(val code: Int, message: String) : Exception(message)

data class PagedResponse<T>(
    val items: List<T>,
    val nextPageToken: String?
)

data class PagedQuery(
    val page: Int,
    val pageSize: Int,
    val nextPageToken: String? = null,
    val filter: DateTimeFilter? = null
) {
    init {
        require(page > 0) { "Page must be greater than 0." }
        require(pageSize in 1..1000) { "Page size must be between 1 and 1000." }
        nextPageToken?.let {
            require(it.isNotBlank()) { "Next page token must not be blank." }
        }
    }
}

data class DateTimeFilter(val startDate: LocalDateTime, val endDate: LocalDateTime) {
    init {
        require(startDate.isBefore(endDate)) { "Start date must be before end date." }
    }
}