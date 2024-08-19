package com.kss.zoom.model.pagination

data class Page<T>(
    val index: Short,
    val size: Short,
    val items: List<T>,
    val nextPageToken: String? = null
)
