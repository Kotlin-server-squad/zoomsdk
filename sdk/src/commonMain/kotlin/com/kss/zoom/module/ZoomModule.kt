package com.kss.zoom.module

import com.kss.zoom.model.CallResult
import com.kss.zoom.model.request.UserRequest

interface ZoomModule {
    suspend fun <T> withAccessToken(request: UserRequest, block: suspend (String) -> CallResult<T>): CallResult<T>
}

