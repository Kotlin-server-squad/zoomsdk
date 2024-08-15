package com.kss.zoom.module.auth.model

import com.kss.zoom.common.currentTimeMillis

data class UserTokens(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val createdAt: Long,
) {
    fun isExpired(): Boolean {
        return (currentTimeMillis() - createdAt) > expiresIn
    }
}
