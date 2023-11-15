package com.kss.zoomsdk.client

@JvmInline
value class ClientId(val value: String)

@JvmInline
value class ClientSecret(val value: String)

class ZoomClientException(cause: Throwable) : RuntimeException(cause)
