package com.kss.zoom.sdk.users

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.ZoomModule
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.toWebClient
import io.ktor.client.*

interface Users : ZoomModule {
    suspend fun create(request: CreateUser): Result<User>
    suspend fun update(id: UserId, request: UpdateUser): Result<User>
    suspend fun delete(id: UserId): Result<User>
    suspend fun list(): Result<List<User>>
}

class UsersImpl private constructor(
    auth: UserTokens,
    client: WebClient
) : ZoomModuleBase(auth, client), Users {
    companion object {
        fun create(auth: UserTokens, httpClient: HttpClient? = null): Users =
            UsersImpl(auth, httpClient.toWebClient())
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
