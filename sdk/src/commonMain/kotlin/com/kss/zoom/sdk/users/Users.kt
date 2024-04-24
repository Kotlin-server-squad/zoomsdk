package com.kss.zoom.sdk.users

import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.sdk.common.model.Page
import com.kss.zoom.sdk.common.toWebClient
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
import io.ktor.client.*

class Users(
    client: WebClient,
    tokens: UserTokens? = null
) : ZoomModuleBase(tokens, client), IUsers {
    constructor(tokens: UserTokens? = null, httpClient: HttpClient? = null) : this(
        httpClient.toWebClient(),
        tokens
    )

    override suspend fun create(request: CreateUser): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun update(id: UserId, request: UpdateUser): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: UserId): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun get(id: UserId): Result<UserInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun checkEmail(email: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserPermissions(id: UserId): Result<UserPermissions> {
        TODO("Not yet implemented")
    }

    override suspend fun list(query: UserPageQuery): Result<Page<GetListUser>> {
        TODO("Not yet implemented")
    }
}
