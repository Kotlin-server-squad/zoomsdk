package com.kss.zoom.sdk.users

import com.kss.zoom.sdk.IZoomModule
import com.kss.zoom.sdk.common.model.Page
import com.kss.zoom.sdk.users.model.UserId
import com.kss.zoom.sdk.users.model.api.*
import com.kss.zoom.sdk.users.model.api.pagination.UserPageQuery
import com.kss.zoom.sdk.users.model.domain.*
import com.kss.zoom.sdk.users.model.domain.UserInfo

interface IUsers : IZoomModule {
    suspend fun create(request: CreateUser): Result<User>
    suspend fun update(id: UserId, request: UpdateUser): Result<Unit>
    suspend fun delete(id: UserId): Result<Unit>
    suspend fun get(id: UserId): Result<UserInfo>
    suspend fun checkEmail(email: String): Result<Boolean>

    suspend fun getUserPermissions(id: UserId): Result<UserPermissions>
    suspend fun list(query: UserPageQuery): Result<Page<GetListUser>>
}