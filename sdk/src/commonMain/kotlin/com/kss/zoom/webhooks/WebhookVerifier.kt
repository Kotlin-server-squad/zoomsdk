package com.kss.zoom.webhooks

import com.kss.zoom.model.ValidationResult
import com.kss.zoom.model.api.Event
import com.kss.zoom.model.request.WebhookRequest

interface WebhookVerifier {
    suspend fun verify(request: WebhookRequest): ValidationResult<Event>
}
