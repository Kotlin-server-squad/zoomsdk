package com.kss.zoom.module.users

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.pagination.Page
import com.kss.zoom.model.pagination.PageRequest
import com.kss.zoom.module.ZoomModuleBase
import com.kss.zoom.module.ZoomModuleConfig
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.users.model.CreateRequest
import com.kss.zoom.module.users.model.UpdateRequest
import com.kss.zoom.module.users.model.User
import kotlinx.datetime.Clock

class DefaultUsers(
    config: ZoomModuleConfig,
    auth: Auth,
    tokenStorage: TokenStorage,
    clock: Clock,
    client: ApiClient,
) :
    ZoomModuleBase(config, auth, tokenStorage, clock), Users {

    override suspend fun create(request: CreateRequest): CallResult<User> {
        TODO("Not yet implemented")
    }

    override suspend fun update(request: UpdateRequest): CallResult<User> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(userId: String): CallResult<User> {
        TODO("Not yet implemented")
    }

    override suspend fun get(userId: String): CallResult<User> {
        TODO("Not yet implemented")
    }

    override suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<User>> {
        TODO("Not yet implemented")
    }
}
