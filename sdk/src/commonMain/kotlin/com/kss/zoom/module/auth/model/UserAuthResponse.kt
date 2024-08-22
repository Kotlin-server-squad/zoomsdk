package com.kss.zoom.module.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String
)

fun UserAuthResponse.toUserTokens(): UserTokens {
    return UserTokens(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}
