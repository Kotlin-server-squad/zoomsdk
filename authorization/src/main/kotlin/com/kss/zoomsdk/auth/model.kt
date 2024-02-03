package com.kss.zoomsdk.auth

@JvmInline
value class ClientId(val value: String)

@JvmInline
value class ClientSecret(val value: String)

class AuthorizationException(cause: Throwable) : RuntimeException(cause)
