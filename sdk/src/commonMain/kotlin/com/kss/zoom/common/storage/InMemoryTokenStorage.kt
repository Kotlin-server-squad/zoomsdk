package com.kss.zoom.common.storage

import com.kss.zoom.module.auth.model.UserTokens
import io.github.reactivecircus.cache4k.Cache
import kotlin.time.Duration.Companion.minutes

class InMemoryTokenStorage : TokenStorage {
    private val refreshTokenCache = Cache.Builder<String, String>().build()

    // Access tokens expire after 1 hour
    // https://developers.zoom.us/docs/integrations/oauth/#refreshing-an-access-token
    private val accessTokenCache = Cache.Builder<String, String>()
        .expireAfterWrite(60.minutes)
        .build()

    override suspend fun saveTokens(userId: String, userTokens: UserTokens) {
        refreshTokenCache.put(userId, userTokens.refreshToken)
        accessTokenCache.put(userTokens.refreshToken, userTokens.accessToken)
    }

    override suspend fun getAccessToken(userId: String): String? {
        return refreshTokenCache.get(userId)?.let { refreshToken ->
            accessTokenCache.get(refreshToken)
        }
    }

    override suspend fun getRefreshToken(userId: String): String? {
        return refreshTokenCache.get(userId)
    }

    override suspend fun deleteTokens(userId: String) {
        refreshTokenCache.get(userId)?.let { refreshToken ->
            accessTokenCache.invalidate(refreshToken)
        }
        refreshTokenCache.invalidate(userId)
    }
}
