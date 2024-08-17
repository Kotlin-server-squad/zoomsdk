package com.kss.zoom.module

import com.kss.zoom.common.extensions.coroutines.flatMap
import com.kss.zoom.common.extensions.coroutines.map
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.Auth

abstract class ZoomModuleBase(
    private val auth: Auth,
    private val tokenStorage: TokenStorage,
) : ZoomModule {
    override suspend fun <T> withAccessToken(userId: String, block: suspend (String) -> CallResult<T>): CallResult<T> {
        tokenStorage.getAccessToken(userId)?.let {
            return block(it)
        }

        val refreshToken = tokenStorage.getRefreshToken(userId)
            ?: return CallResult.Error("Neither access nor refresh token found for $userId")

        return auth.reauthorize(refreshToken).map {
            tokenStorage.saveTokens(userId, it)
            it.accessToken
        }.flatMap { block(it) }
    }
}
