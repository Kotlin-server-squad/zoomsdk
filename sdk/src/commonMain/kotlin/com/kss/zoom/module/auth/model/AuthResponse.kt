package com.kss.zoom.module.auth.model

import com.kss.zoom.common.currentTimeMillis
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Long
)

fun AuthResponse.toUserTokens(): UserTokens {
    return UserTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresIn = expiresIn,
        createdAt = currentTimeMillis()
    )
}
