package com.kss.zoom.module

import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.Auth

interface ZoomModule {
    suspend fun <T> withAccessToken(auth: Auth, block: suspend (String) -> CallResult<T>): CallResult<T> {
        return when (val token = auth.accessToken()) {
            is CallResult.Success -> block(token.data)
            else -> CallResult.Error("Access token is not available")
        }
    }
}
