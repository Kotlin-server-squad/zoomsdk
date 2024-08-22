package com.kss.zoom.module.auth.model

data class UserTokens(
    val accessToken: String,
    val refreshToken: String
)
