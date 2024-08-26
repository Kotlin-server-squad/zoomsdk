package com.kss.zoom.module.users

import com.kss.zoom.model.CallResult
import com.kss.zoom.model.pagination.Page
import com.kss.zoom.model.request.ListRequest
import com.kss.zoom.module.ZoomModule
import com.kss.zoom.module.users.model.*

interface Users : ZoomModule {
    suspend fun create(request: CreateRequest): CallResult<User>
    suspend fun update(request: UpdateRequest): CallResult<User>
    suspend fun get(request: GetRequest): CallResult<User>
    suspend fun delete(request: DeleteRequest): CallResult<Unit>
    suspend fun list(request: ListRequest): CallResult<Page<User>>
}

