package com.kss.zoom.sdk.users

import com.kss.zoom.sdk.IZoomModule
import com.kss.zoom.sdk.common.model.Page
import com.kss.zoom.sdk.users.model.Email
import com.kss.zoom.sdk.users.model.domain.User
import com.kss.zoom.sdk.users.model.UserId
import com.kss.zoom.sdk.users.model.api.GetListUser
import com.kss.zoom.sdk.users.model.domain.CreateUser
import com.kss.zoom.sdk.users.model.domain.UpdateUser
import com.kss.zoom.sdk.users.model.domain.UserInfo
import com.kss.zoom.sdk.users.model.domain.UserPermissions
import com.kss.zoom.sdk.users.model.pagination.UserPageQuery

interface IUsers : IZoomModule {
    suspend fun create(request: CreateUser): Result<User>
    suspend fun update(id: UserId, request: UpdateUser): Result<Unit>
    suspend fun delete(id: UserId): Result<Unit>
    suspend fun get(id: UserId): Result<UserInfo>
    suspend fun checkEmail(email: Email): Result<Boolean>
    suspend fun getUserPermissions(id: UserId): Result<UserPermissions>
    suspend fun list(query: UserPageQuery): Result<Page<GetListUser>>
}