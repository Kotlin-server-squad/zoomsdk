package com.kss.zoom.sdk.users

import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.sdk.common.ZOOM_API_URL
import com.kss.zoom.sdk.common.model.Page
import com.kss.zoom.sdk.common.toWebClient
import com.kss.zoom.sdk.users.model.Email
import com.kss.zoom.sdk.users.model.UserId
import com.kss.zoom.sdk.users.model.api.*
import com.kss.zoom.sdk.users.model.domain.*
import com.kss.zoom.sdk.users.model.domain.UserInfo
import com.kss.zoom.sdk.users.model.pagination.PaginationObject
import com.kss.zoom.sdk.users.model.pagination.UserPageQuery
import com.kss.zoom.sdk.users.model.pagination.toDomain
import io.ktor.client.*

class Users(
    private val client: WebClient,
    tokens: UserTokens? = null
) : ZoomModuleBase(tokens, client), IUsers {
    constructor(tokens: UserTokens? = null, httpClient: HttpClient? = null) : this(
        httpClient.toWebClient(),
        tokens
    )

    override suspend fun create(request: CreateUser): Result<User> {
        return client.post<CreateUserResponse>(
            url = "$ZOOM_API_URL/users",
            token = userTokens!!.accessToken.value,
            contentType = WebClient.JSON_CONTENT_TYPE,
            body = request.toApi()
        ).map {
            it.toDomain()
        }
    }

    override suspend fun update(id: UserId, request: UpdateUser): Result<Unit> {
        return client.patch(
            url = "$ZOOM_API_URL/users/$id",
            token = userTokens!!.accessToken.value,
            contentType = WebClient.JSON_CONTENT_TYPE,
            body = request.toApi()
        )
    }

    override suspend fun delete(id: UserId): Result<Unit> {
        return client.delete(
            url = "$ZOOM_API_URL/users/$id",
            token = userTokens!!.accessToken.value,
        )
    }

    override suspend fun get(id: UserId): Result<UserInfo> {
        return client.get<GetUser>(
            url = "$ZOOM_API_URL/users/$id",
            token = userTokens!!.accessToken.value,
        ).map {
            it.toDomain()
        }
    }

    override suspend fun checkEmail(email: Email): Result<Boolean> {
        return client.get<CheckEmailResponse>(
            url = "$ZOOM_API_URL/users/email?email=$email",
            token = userTokens!!.accessToken.value,
        ).map {
            it.existedEmail ?: false
        }
    }

    override suspend fun getUserPermissions(id: UserId): Result<UserPermissions> {
        return client.get<UserPermissionsResponse>(
            url = "$ZOOM_API_URL/users/$id/permissions",
            token = userTokens!!.accessToken.value,
        ).map {
            it.toDomain()
        }
    }

    override suspend fun list(query: UserPageQuery): Result<Page<GetListUser>> {
        val params = StringBuilder("page_number=${query.pageNumber}&page_size=${query.pageSize}")
        query.status?.let {
            params.append("&status=$it")
        }
        query.roleId?.let {
            params.append("&role_id=$it")
        }
        query.nextPageToken?.let {
            params.append("&next_page_token=$it")
        }
        return client.get<PaginationObject>(
            url = "$ZOOM_API_URL/users?$params",
            token = userTokens!!.accessToken.value
        ).map { it.toDomain() }
    }
}
