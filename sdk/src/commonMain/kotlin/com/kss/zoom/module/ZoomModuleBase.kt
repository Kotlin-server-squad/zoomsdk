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
                return CallResult.Error.Other(e.message ?: "Validation failed")
            }
        }
        return tryCall {
            tokenStorage.getAccessToken(request.userId)?.let {
                return@tryCall block(it)
            }
            val refreshToken = tokenStorage.getRefreshToken(request.userId)
                ?: run {
                    // Server-to-server authorization using app credentials. The access token is valid for 1 hour.
                    return@tryCall withAccountAccessToken { block(it) }
                }

            auth.reauthorize(refreshToken).map {
                tokenStorage.saveUserTokens(request.userId, it)
                it.accessToken
            }.flatMap {
                block(it)
            }
        }
    }

    private suspend fun <T> withAccountAccessToken(
        block: suspend (String) -> CallResult<T>,
    ): CallResult<T> {
        return tryCall {
            tokenStorage.getAccountAccessToken(auth.accountId)?.let {
                return@tryCall block(it)
            }
            auth.authorizeAccount().map { accountToken ->
                tokenStorage.saveAccountToken(auth.accountId, accountToken.accessToken)
                accountToken.accessToken
            }.flatMap { block(it) }
        }
    }
}
