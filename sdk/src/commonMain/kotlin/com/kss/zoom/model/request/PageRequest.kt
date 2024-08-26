package com.kss.zoom.model.request

import com.kss.zoom.common.greaterZero
import com.kss.zoom.model.pagination.filter.PageFilter

data class PageRequest(
    val index: Short = 1,
    val size: Short = 30,
    val filters: List<PageFilter> = emptyList(),
    val nextPageToken: String? = null,
) {
    init {
        index.greaterZero("index")
        require(size in 1..1000) { "Page size must be between 1 and 1000." }
        nextPageToken?.isNotBlank()
    }
}
