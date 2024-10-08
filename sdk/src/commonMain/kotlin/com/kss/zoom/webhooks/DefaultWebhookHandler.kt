package com.kss.zoom.webhooks

import com.kss.zoom.common.event.DefaultEventHandler.Companion.handler
import com.kss.zoom.common.event.DynamicEventHandler
import com.kss.zoom.common.event.EventHandler
import com.kss.zoom.common.tryCall
import com.kss.zoom.common.validation.call
import com.kss.zoom.model.request.WebhookRequest

class DefaultWebhookHandler(
    private val verifier: WebhookVerifier = DefaultWebhookVerifier(),
) : WebhookHandler {

    companion object {
        fun webhook(init: WebhookHandler.() -> Unit): WebhookHandler {
            return DefaultWebhookHandler().apply(init)
        }
    }

    private val handlers = mutableMapOf<EventType, EventHandler>()

    private var errorHandler: suspend (WebhookRequest, Exception) -> Unit = { _, _ -> }

    override fun register(eventType: EventType, eventHandler: EventHandler): WebhookHandler {
        handlers[eventType] = eventHandler
        return this
    }

    override fun onError(block: suspend (WebhookRequest, Exception) -> Unit): WebhookHandler {
        errorHandler = block
        return this
    }

    override suspend fun handle(request: WebhookRequest) {
        tryCall({ errorHandler(request, it) }) {
            // Verify the request and get the event
            val event = call { verifier.verify(request) }
            // Invoke the handler for the event, if it exists
            handlers[event.name]?.on(event)
        }

    }

    override fun handler(eventType: String, handler: DynamicEventHandler.() -> Unit) {
        this.register(eventType, handler(handler))
    }
}

