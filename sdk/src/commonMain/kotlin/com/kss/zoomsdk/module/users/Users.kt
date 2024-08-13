package com.kss.zoomsdk.module.users

import com.kss.zoomsdk.model.CallResult
import com.kss.zoomsdk.model.Page
import com.kss.zoomsdk.model.PageRequest
import com.kss.zoomsdk.module.users.model.CreateRequest
import com.kss.zoomsdk.module.users.model.UpdateRequest
import com.kss.zoomsdk.module.users.model.User

interface Users {
    suspend fun create(request: CreateRequest): CallResult<User>
    suspend fun update(request: UpdateRequest): CallResult<User>
    suspend fun delete(userId: String): CallResult<User>
    suspend fun get(userId: String): CallResult<User>
    suspend fun me(): CallResult<User>
    suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<User>>
}
