package com.kss.zoom.module

import com.kss.zoom.common.extensions.coroutines.flatMap
import com.kss.zoom.common.extensions.coroutines.map
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.common.tryCall
import com.kss.zoom.model.CallResult
import com.kss.zoom.model.request.TimeAwareUserRequest
import com.kss.zoom.model.request.UserRequest
import com.kss.zoom.module.auth.Auth
import kotlinx.datetime.Clock

abstract class ZoomModuleBase(
    private val config: ZoomModuleConfig,
    private val auth: Auth,
    private val tokenStorage: TokenStorage,
    private val clock: Clock,
) : ZoomModule {

    fun url(path: String): String = "${config.baseUrl}$path"

    override suspend fun <T> withAccessToken(
        request: UserRequest,
        block: suspend (String) -> CallResult<T>,
    ): CallResult<T> {
        if (request is TimeAwareUserRequest) {
            try {
                request.validate(clock)
            } catch (e: IllegalArgumentException) {
                return CallResult.Error(e.message ?: "Validation failed")
            }
        }
        return tryCall {
            tokenStorage.getAccessToken(request.userId)?.let {
                return@tryCall block(it)
            }
            val refreshToken = tokenStorage.getRefreshToken(request.userId)
                ?: return@tryCall CallResult.Error("Neither access nor refresh token found for ${request.userId}")

            auth.reauthorize(refreshToken).map {
                tokenStorage.saveTokens(request.userId, it)
                it.accessToken
            }.flatMap {
                block(it)
            }
        }


    }
}
