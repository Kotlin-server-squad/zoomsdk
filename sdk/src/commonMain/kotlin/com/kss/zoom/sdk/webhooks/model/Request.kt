package com.kss.zoom.sdk.webhooks.model

data class Request(
    val payload: String,
    val timestamp: Long?,
    val signature: String?
)
