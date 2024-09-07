package com.kss.zoom.webhooks

import com.kss.zoom.model.validation.ValidationResult
import com.kss.zoom.model.api.event.Event
import com.kss.zoom.model.request.WebhookRequest

class DefaultWebhookVerifier : WebhookVerifier {
    override suspend fun verify(request: WebhookRequest): ValidationResult<Event> {
        TODO("Not yet implemented")
    }
}
