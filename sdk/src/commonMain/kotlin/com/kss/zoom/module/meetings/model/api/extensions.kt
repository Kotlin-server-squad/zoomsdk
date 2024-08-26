package com.kss.zoom.module.meetings.model.api

import com.kss.zoom.model.pagination.Page
import com.kss.zoom.model.pagination.PaginationObject
import com.kss.zoom.module.meetings.model.Meeting

fun PaginationObject<MeetingResponse>.toModel(): Page<Meeting> =
    Page(
        index = pageNumber,
        size = pageSize,
        items = records.map { it.toModel() },
        nextPageToken = nextPageToken
    )
