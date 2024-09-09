package com.kss.zoom.model.request

data class WebhookRequest(
    val signature: String,
    val timestamp: Long,
    val body: String,
)
