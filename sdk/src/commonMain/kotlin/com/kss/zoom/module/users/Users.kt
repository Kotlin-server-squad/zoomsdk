package com.kss.zoom.module.users

import com.kss.zoom.model.CallResult
import com.kss.zoom.model.pagination.Page
import com.kss.zoom.model.pagination.PageRequest
import com.kss.zoom.module.ZoomModule
import com.kss.zoom.module.users.model.CreateRequest
import com.kss.zoom.module.users.model.UpdateRequest
import com.kss.zoom.module.users.model.User

interface Users : ZoomModule {
    suspend fun create(request: CreateRequest): CallResult<User>
    suspend fun update(request: UpdateRequest): CallResult<User>
    suspend fun delete(userId: String): CallResult<User>
    suspend fun get(userId: String): CallResult<User>
    suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<User>>
}

