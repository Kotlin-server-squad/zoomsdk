package com.kss.zoom.sdk.common.model

data class Page<T>(
    val items: List<T>,
    val pageNumber: Int,
    val pageCount: Int,
    val pageSize: Int,
    val totalRecords: Int,
    val nextPageToken: String? = null
)
