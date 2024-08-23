package com.kss.zoom.module.meetings

import com.kss.zoom.model.CallResult
import com.kss.zoom.model.pagination.Page
import com.kss.zoom.module.ZoomModule
import com.kss.zoom.module.meetings.model.*

interface Meetings : ZoomModule {
    suspend fun create(request: CreateRequest): CallResult<Meeting>
    suspend fun update(request: UpdateRequest): CallResult<Meeting>
    suspend fun get(request: GetRequest): CallResult<Meeting>
    suspend fun delete(request: DeleteRequest): CallResult<Unit>
    suspend fun deleteAll(request: DeleteAllRequest): CallResult<Int>
    suspend fun list(request: ListRequest): CallResult<Page<Meeting>>
}

