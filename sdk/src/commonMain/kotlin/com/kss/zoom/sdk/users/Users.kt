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

class Users(
    client: WebClient,
    tokens: UserTokens? = null
) : ZoomModuleBase(tokens, client), IUsers {
    constructor(tokens: UserTokens? = null, httpClient: HttpClient? = null) : this(
        httpClient.toWebClient(),
        tokens
    )

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
