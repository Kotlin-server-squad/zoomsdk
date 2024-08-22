package com.kss.zoom.common.storage

import com.kss.zoom.module.auth.model.UserTokens
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class InMemoryTokenStorage(config: InMemoryTokenStorageConfig = InMemoryTokenStorageConfig.DEFAULT) : TokenStorage {
    private val refreshTokenCache = Cache.Builder<String, String>().build()

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    // Access tokens expire after 1 hour
    // https://developers.zoom.us/docs/integrations/oauth/#refreshing-an-access-token
    private val accessTokenCache = Cache.Builder<String, String>()
        .expireAfterWrite(config.accessTokenExpiry)
        .build()

    // Access tokens expire after 1 hour
    // https://developers.zoom.us/docs/internal-apps/s2s-oauth/#generate-access-token
    private val accountAccessTokenCache = Cache.Builder<String, String>()
        .expireAfterWrite(config.accessTokenExpiry)
        .build()

    override suspend fun saveUserTokens(userId: String, userTokens: UserTokens): Unit = withContext(ioContext) {
        coroutineScope {
            launch { refreshTokenCache.put(userId, userTokens.refreshToken) }
            launch { accessTokenCache.put(userTokens.refreshToken, userTokens.accessToken) }
        }
    }

    override suspend fun getAccessToken(userId: String): String? = withContext(ioContext) {
        refreshTokenCache.get(userId)?.let { refreshToken ->
            accessTokenCache.get(refreshToken)
        }
    }

    override suspend fun getRefreshToken(userId: String): String? = withContext(ioContext) {
        refreshTokenCache.get(userId)
    }

    override suspend fun deleteTokens(userId: String) = withContext(ioContext) {
        refreshTokenCache.get(userId)?.let { refreshToken ->
            accessTokenCache.invalidate(refreshToken)
        }
        refreshTokenCache.invalidate(userId)
    }

    override suspend fun saveAccountToken(accountId: String, accessToken: String): Unit = withContext(ioContext) {
        accountAccessTokenCache.put(accountId, accessToken)
    }

    override suspend fun getAccountAccessToken(accountId: String): String? = withContext(ioContext) {
        accountAccessTokenCache.get(accountId)
    }
}
