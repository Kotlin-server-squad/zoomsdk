package com.kss.zoom.sdk.common.model

data class PagedQuery(
    val pageNumber: Int,
    val pageSize: Int,
    val nextPageToken: String? = null,
    val filter: DateTimeFilter? = null
) {
    init {
        require(pageNumber > 0) { "Page number must be greater than 0." }
        require(pageSize in 1..1000) { "Page size must be between 1 and 1000." }
        nextPageToken?.let {
            require(it.isNotBlank()) { "Next page token must not be blank." }
        }
    }
}