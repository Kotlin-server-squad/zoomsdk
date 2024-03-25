package com.kss.zoom.sdk.users

import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.sdk.common.toWebClient
import com.kss.zoom.sdk.users.model.CreateUserCommand
import com.kss.zoom.sdk.users.model.UpdateUserCommand
import com.kss.zoom.sdk.users.model.User
import com.kss.zoom.sdk.users.model.UserId
import io.ktor.client.*

class Users private constructor(
    tokens: UserTokens? = null,
    client: WebClient
) : ZoomModuleBase(tokens, client), IUsers {
    companion object {
        fun create(tokens: UserTokens? = null, httpClient: HttpClient? = null): IUsers =
            Users(tokens, httpClient.toWebClient())
    }

    override suspend fun create(command: CreateUserCommand): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun update(command: UpdateUserCommand): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: UserId): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun list(): Result<List<User>> {
        TODO("Not yet implemented")
    }
}
