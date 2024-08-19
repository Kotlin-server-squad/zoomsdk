package com.kss.zoom.module.meetings.model

import com.kss.zoom.model.pagination.PageRequest

data class ListRequest(
    val userId: String,
    val pageRequest: PageRequest
)
