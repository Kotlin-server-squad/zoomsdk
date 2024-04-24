package com.kss.zoom.sdk.users

import com.kss.zoom.sdk.common.model.Page
import com.kss.zoom.sdk.users.model.CreateUserCommand
import com.kss.zoom.sdk.users.model.UpdateUserCommand
import com.kss.zoom.sdk.users.model.User
import com.kss.zoom.sdk.users.model.UserId
import com.kss.zoom.sdk.users.model.api.GetListUser
import com.kss.zoom.sdk.users.model.domain.CreateUser
import com.kss.zoom.sdk.users.model.domain.UpdateUser
import com.kss.zoom.sdk.users.model.domain.UserInfo
import com.kss.zoom.sdk.users.model.domain.UserPermissions
import com.kss.zoom.sdk.users.model.pagination.UserPageQuery

interface IUsers {
    suspend fun create(request: CreateUser): Result<User>
    suspend fun update(id: UserId, request: UpdateUser): Result<Unit>
    suspend fun delete(id: UserId): Result<Unit>
    suspend fun get(id: UserId): Result<UserInfo>
    suspend fun checkEmail(email: String): Result<Boolean>
    suspend fun getUserPermissions(id: UserId): Result<UserPermissions>
    suspend fun list(query: UserPageQuery): Result<Page<GetListUser>>
}