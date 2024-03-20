package com.kss.zoom.sdk.users

import com.kss.zoom.sdk.IZoomModule
import com.kss.zoom.sdk.users.model.CreateUserCommand
import com.kss.zoom.sdk.users.model.UpdateUserCommand
import com.kss.zoom.sdk.users.model.User
import com.kss.zoom.sdk.users.model.UserId

interface IUsers : IZoomModule {
    suspend fun create(command: CreateUserCommand): Result<User>
    suspend fun update(command: UpdateUserCommand): Result<User>
    suspend fun delete(id: UserId): Result<User>
    suspend fun list(): Result<List<User>>
}