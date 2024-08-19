package com.kss.zoom.module

import com.kss.zoom.common.extensions.coroutines.flatMap
import com.kss.zoom.common.extensions.coroutines.map
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.request.TimeAwareUserRequest
import com.kss.zoom.model.request.UserRequest
import com.kss.zoom.module.auth.Auth
import kotlinx.datetime.Clock

abstract class ZoomModuleBase(
    private val auth: Auth,
    private val tokenStorage: TokenStorage,
    private val clock: Clock,
) : ZoomModule {
    override suspend fun <T> withAccessToken(
        request: UserRequest,
        block: suspend (String) -> CallResult<T>,
    ): CallResult<T> {
        if (request is TimeAwareUserRequest) {
            request.validate(clock)
        }
        tokenStorage.getAccessToken(request.userId)?.let {
            return block(it)
        }

        val refreshToken = tokenStorage.getRefreshToken(request.userId)
            ?: return CallResult.Error("Neither access nor refresh token found for ${request.userId}")

        return auth.reauthorize(refreshToken).map {
            tokenStorage.saveTokens(request.userId, it)
            it.accessToken
        }.flatMap {
            // TODO try-catch and map to CallResult.Error
            block(it)
        }
    }
}
