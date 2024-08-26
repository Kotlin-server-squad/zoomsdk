package com.kss.zoom.module.users

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.extensions.coroutines.flatMap
import com.kss.zoom.common.extensions.coroutines.map
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.pagination.Page
import com.kss.zoom.model.pagination.PaginationObject
import com.kss.zoom.model.request.ListRequest
import com.kss.zoom.module.ZoomModuleBase
import com.kss.zoom.module.ZoomModuleConfig
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.users.model.*
import com.kss.zoom.module.users.model.api.UserResponse
import com.kss.zoom.module.users.model.api.toModel
import kotlinx.datetime.Clock

class DefaultUsers(
    config: ZoomModuleConfig,
    auth: Auth,
    tokenStorage: TokenStorage,
    clock: Clock,
    private val client: ApiClient,
) :
    ZoomModuleBase(config, auth, tokenStorage, clock), Users {

    override suspend fun create(request: CreateRequest): CallResult<User> = withAccessToken(request) { token ->
        client.post<UserResponse>(
            url = url("/users"),
            token = token,
            contentType = "application/json",
            body = request.toApi()
        ).map { it.toModel() }
    }

    override suspend fun update(request: UpdateRequest): CallResult<User> = withAccessToken(request) { token ->
        client.patch<Unit>(
            url = url("/users/${request.userId}"),
            token = token,
            contentType = "application/json",
            body = request.toApi()
        ).flatMap {
            get(GetRequest(request.userId))
        }
    }

    override suspend fun get(request: GetRequest): CallResult<User> = withAccessToken(request) { token ->
        client.get<UserResponse>(
            url = url("/users/${request.userId}"),
            token = token
        ).map { it.toModel() }
    }

    override suspend fun delete(request: DeleteRequest): CallResult<Unit> = withAccessToken(request) { token ->
        client.delete<Unit>(
            url = url("/users/${request.userId}"),
            token = token
        )
    }


    override suspend fun list(request: ListRequest): CallResult<Page<User>> = withAccessToken(request) { token ->
        val params =
            StringBuilder("page_number=${request.pageRequest.index}&page_size=${request.pageRequest.size}")
        request.pageRequest.filters.forEach { params.append("&${it.toQueryString()}") }
        request.pageRequest.nextPageToken?.let { params.append("&next_page_token=$it") }
        client.get<PaginationObject<UserResponse>>(
            url = url("/users?$params"),
            token = token
        ).map { it.toModel() }
    }
}
