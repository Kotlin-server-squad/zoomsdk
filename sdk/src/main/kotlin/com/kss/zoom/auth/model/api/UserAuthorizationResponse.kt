package com.kss.zoom.auth.model.api

import com.kss.zoom.auth.model.AccessToken
import com.kss.zoom.auth.model.RefreshToken
import com.kss.zoom.auth.model.UserTokens
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserAuthorizationResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long
) {
    fun toUserTokens(): UserTokens {
        return UserTokens(
            accessToken = AccessToken(accessToken, expiresIn),
            refreshToken = RefreshToken(refreshToken)
        )
    }
}
