package com.kss.zoom.webhooks

import com.kss.zoom.common.tryCall
import com.kss.zoom.model.api.Event
import com.kss.zoom.model.request.WebhookRequest
import com.kss.zoom.model.ValidationResult
import kotlinx.serialization.json.Json

class DefaultWebhookVerifier: WebhookVerifier {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun verify(request: WebhookRequest): ValidationResult<Event> {
        return tryCall({ exception ->
            val message = "Failed to verify webhook request: ${exception.message ?: "Unknown error"}"
            ValidationResult.Error(message)
        }) {
            // TODO Implement verification logic
            val event = jsonSerializer.decodeFromString<Event>(request.body)
            ValidationResult.Success(event)
        }
    }
}
