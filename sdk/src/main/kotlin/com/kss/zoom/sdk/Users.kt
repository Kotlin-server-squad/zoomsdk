package com.kss.zoom.sdk.users

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.ZoomModule
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.toWebClient
import io.ktor.client.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*

interface Users : ZoomModule {
    suspend fun create(request: CreateUserRequest): Result<CreateUserResponse>
    suspend fun update(id: UserId, request: UpdateUser): Result<User>
    suspend fun delete(id: UserId): Result<Unit>
    suspend fun get(id: UserId): Result<GetUser>

    suspend fun list(): Result<List<GetUsersResponse>>
}

class UsersImpl private constructor(
    private val auth: UserTokens,
    private val client: WebClient
) : ZoomModuleBase(auth, client), Users {
    companion object {
        const val ZOOM_API_URL = "https://api.zoom.us/v2"
        fun create(auth: UserTokens, httpClient: HttpClient? = null): Users =
            UsersImpl(auth, httpClient.toWebClient())
    }

    override suspend fun create(request: CreateUserRequest): Result<CreateUserResponse> {
        return client.post<CreateUserResponse>(
            url = "$ZOOM_API_URL/users",
            token = auth.accessToken.value,
            contentType = WebClient.JSON_CONTENT_TYPE,
            body = request
        )
    }

    override suspend fun update(id: UserId, request: UpdateUser): Result<User> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: UserId): Result<Unit> {
        return client.delete(
            url = "$ZOOM_API_URL/users/$id",
            token = auth.accessToken.value,
        )
    }

    override suspend fun get(id: UserId): Result<GetUser> {
        return client.get(
            url = "$ZOOM_API_URL/users/$id",
            token = auth.accessToken.value,
        )
    }

    override suspend fun list(): Result<List<GetUsersResponse>> {
        return client.get(
            url = "$ZOOM_API_URL/users",
            token = auth.accessToken.value,
        )
    }
}
