package com.kss.zoom.auth.model.api

import com.kss.zoom.auth.model.AccessToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerAuthorizationResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Long
) {
    fun toAccessToken(): AccessToken = AccessToken(accessToken, expiresIn)
}
