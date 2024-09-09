package com.kss.zoom.webhooks

import com.kss.zoom.common.event.EventHandler
import com.kss.zoom.model.request.WebhookRequest

typealias EventType = String

interface WebhookHandler {
    // Register a new event type and its corresponding mapper.
    // The mapper will be used to map the incoming event to a typed custom object.
    fun register(eventType: EventType, mapper: EventHandler): WebhookHandler

    // When a verification fails or when an error occurs during the handling of a webhook request
    // the onError block will be called. You can use it to queue up the original request in a DLQ, for example.
    suspend fun onError(block: suspend (WebhookRequest, Throwable) -> Unit)

    // Entry point for handling incoming webhook requests
    suspend fun handle(request: WebhookRequest)
}
