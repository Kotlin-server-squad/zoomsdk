package com.kss.zoom.module.users.model.api

import com.kss.zoom.model.pagination.Page
import com.kss.zoom.model.pagination.PaginationObject
import com.kss.zoom.module.users.model.User

fun PaginationObject<UserResponse>.toModel(): Page<User> =
    Page(
        index = pageNumber,
        size = pageSize,
        items = records.map { it.toModel() },
        nextPageToken = nextPageToken
    )
