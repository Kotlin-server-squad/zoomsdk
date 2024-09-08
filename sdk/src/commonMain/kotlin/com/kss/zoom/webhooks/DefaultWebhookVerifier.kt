package com.kss.zoom.webhooks

import com.kss.zoom.common.tryCall
import com.kss.zoom.model.api.event.Event
import com.kss.zoom.model.request.WebhookRequest
import com.kss.zoom.model.validation.ValidationResult
import kotlinx.serialization.json.Json

class DefaultWebhookVerifier: WebhookVerifier {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun verify(request: WebhookRequest): ValidationResult<Event> {
        return tryCall({ throwable ->
            val message = "Failed to verify webhook request: ${throwable.message ?: "Unknown error"}"
            ValidationResult.Error(message)
        }) {
            // TODO Implement verification logic
            val event = jsonSerializer.decodeFromString<Event>(request.body)
            ValidationResult.Success(event)
        }
    }
}
