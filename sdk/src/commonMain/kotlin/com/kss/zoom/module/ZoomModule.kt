package com.kss.zoom.module

import com.kss.zoom.model.CallResult

interface ZoomModule {
    suspend fun <T> withAccessToken(userId: String, block: suspend (String) -> CallResult<T>): CallResult<T>
}

