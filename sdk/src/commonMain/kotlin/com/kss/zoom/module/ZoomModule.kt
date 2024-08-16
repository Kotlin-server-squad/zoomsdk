package com.kss.zoom.module

import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.Auth

interface ZoomModule {
    suspend fun <T> withAccessToken(userId: String, block: suspend (String) -> CallResult<T>): CallResult<T>
}

abstract class BaseZoomModule(
    private val auth: Auth,
    private val tokenStorage: TokenStorage,
) : ZoomModule {
    override suspend fun <T> withAccessToken(userId: String, block: suspend (String) -> CallResult<T>): CallResult<T> {
        val accessToken = tokenStorage.getAccessToken(userId)
        return if (accessToken != null) {
            block(accessToken)
        } else {
            CallResult.Error("Access token not found")
        }
    }
}
