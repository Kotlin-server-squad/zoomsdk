package com.kss.zoom.module.users

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.Page
import com.kss.zoom.model.PageRequest
import com.kss.zoom.module.BaseZoomModule
import com.kss.zoom.module.ZoomModule
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.users.model.CreateRequest
import com.kss.zoom.module.users.model.UpdateRequest
import com.kss.zoom.module.users.model.User

interface Users : ZoomModule {
    companion object {
        fun create(auth: Auth, tokenStorage: TokenStorage, client: ApiClient): Users {
            return DefaultUsers(auth, tokenStorage, client)
        }
    }

    suspend fun create(request: CreateRequest): CallResult<User>
    suspend fun update(request: UpdateRequest): CallResult<User>
    suspend fun delete(userId: String): CallResult<User>
    suspend fun get(userId: String): CallResult<User>
    suspend fun list(userId: String, pageRequest: PageRequest): CallResult<Page<User>>
}

private class DefaultUsers(auth: Auth, tokenStorage: TokenStorage, client: ApiClient) :
    BaseZoomModule(auth, tokenStorage), Users {

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
