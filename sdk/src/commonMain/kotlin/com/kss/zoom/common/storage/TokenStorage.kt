package com.kss.zoom.common.storage

import com.kss.zoom.module.auth.model.UserTokens

interface TokenStorage {
    suspend fun saveUserTokens(userId: String, userTokens: UserTokens)
    suspend fun getAccessToken(userId: String): String?
    suspend fun getRefreshToken(userId: String): String?
    suspend fun deleteTokens(userId: String)
    suspend fun saveAccountToken(accountId: String, accessToken: String)
    suspend fun getAccountAccessToken(accountId: String): String?
}
