package com.kss.zoom.examples.webhooks

import com.kss.zoom.model.request.WebhookRequest
import com.kss.zoom.webhooks.WebhookHandler

suspend fun WebhookHandler.handle(json: String) {
    this.handle(
        WebhookRequest(
            signature = "signature",
            timestamp = 0L,
            body = json
        )
    )
}
