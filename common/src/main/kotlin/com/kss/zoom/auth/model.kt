package com.kss.zoom.auth

@JvmInline
value class ClientId(val value: String)

@JvmInline
value class ClientSecret(val value: String)

@JvmInline
value class AccountId(val value: String)

@JvmInline
value class AuthorizationCode(val value: String)

data class AccessToken(val value: String, val expiresIn: Long)

@JvmInline
value class RefreshToken(val value: String)

data class UserTokens(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken? = null
)

