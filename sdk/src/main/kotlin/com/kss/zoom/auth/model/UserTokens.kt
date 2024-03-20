package com.kss.zoom.auth.model

data class UserTokens(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken? = null
)
