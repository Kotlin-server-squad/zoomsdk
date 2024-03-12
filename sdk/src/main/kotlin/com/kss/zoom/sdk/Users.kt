package com.kss.zoom.sdk

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.model.domain.users.CreateUser
import com.kss.zoom.sdk.model.domain.users.UpdateUser
import com.kss.zoom.sdk.model.domain.users.User
import com.kss.zoom.sdk.model.domain.users.UserId
import com.kss.zoom.toWebClient
import io.ktor.client.*

interface Users : ZoomModule {
    suspend fun create(request: CreateUser): Result<User>
    suspend fun update(id: UserId, request: UpdateUser): Result<User>
    suspend fun delete(id: UserId): Result<User>
    suspend fun list(): Result<List<User>>
}

class UsersImpl private constructor(
    tokens: UserTokens? = null,
    client: WebClient
) : ZoomModuleBase(tokens, client), Users {
    companion object {
        fun create(tokens: UserTokens? = null, httpClient: HttpClient? = null): Users =
            UsersImpl(tokens, httpClient.toWebClient())
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
