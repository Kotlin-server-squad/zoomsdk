package com.kss.zoom.module.meetings.model

import com.kss.zoom.model.pagination.PageRequest
import com.kss.zoom.model.request.UserRequest

data class ListRequest(
    override val userId: String,
    val pageRequest: PageRequest
) : UserRequest
