package com.kss.zoom.model.request

import com.kss.zoom.model.api.event.Event

data class WebhookRequest(
    val signature: String,
    val timestamp: String,
    val body: Event,
)
