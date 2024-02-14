package com.kss.zoom.sdk.users

import com.kss.zoom.ZoomModule
import com.kss.zoom.ZoomModuleBase
import com.kss.zoom.auth.Authorization

interface Users : ZoomModule {
    suspend fun create(request: CreateUser): Result<User>
    suspend fun update(id: UserId, request: UpdateUser): Result<User>
    suspend fun delete(id: UserId): Result<User>
    suspend fun list(): Result<List<User>>
}

class UsersImpl private constructor(auth: Authorization) : ZoomModuleBase(auth), Users {
    companion object {
        fun create(authorization: Authorization): Users = UsersImpl(authorization)
    }

    override suspend fun create(request: CreateUser): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun update(id: UserId, request: UpdateUser): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: UserId): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun list(): Result<List<User>> {
        TODO("Not yet implemented")
    }
}
