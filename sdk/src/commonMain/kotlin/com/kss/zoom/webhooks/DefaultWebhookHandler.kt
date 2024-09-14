package com.kss.zoom.webhooks

import com.kss.zoom.common.event.DefaultEventHandler.Companion.handler
import com.kss.zoom.common.event.EventHandler
import com.kss.zoom.common.tryCall
import com.kss.zoom.common.validation.call
import com.kss.zoom.model.context.DynamicProperty.Companion.nullable
import com.kss.zoom.model.context.DynamicProperty.Companion.required
import com.kss.zoom.model.request.WebhookRequest
import kotlinx.coroutines.runBlocking

class DefaultWebhookHandler(
    private val verifier: WebhookVerifier = DefaultWebhookVerifier(),
) : WebhookHandler {

    companion object {
        fun webhook(eventType: String, eventHandler: () -> EventHandler): WebhookHandler {
            return DefaultWebhookHandler().register(eventType, eventHandler())
        }
    }

    private val handlers = mutableMapOf<EventType, EventHandler>()

    private var errorHandler: suspend (WebhookRequest, Throwable) -> Unit = { _, _ -> }

    override fun register(eventType: EventType, mapper: EventHandler): WebhookHandler {
        handlers[eventType] = mapper
        return this
    }

    override suspend fun onError(block: suspend (WebhookRequest, Throwable) -> Unit) {
        errorHandler = block
    }

    override suspend fun handle(request: WebhookRequest) {
        tryCall({ errorHandler(request, it) }) {
            // Verify the request and get the event
            val event = call { verifier.verify(request) }
            // Invoke the handler for the event, if it exists
            handlers[event.name]?.on(event)
        }

    }
}

fun main() = runBlocking {
    // Create a webhook handler, a single instance will do.
    val webhookHandler = DefaultWebhookHandler()
        .register("meeting.started", handler {

            // Define the properties of your custom business object here.
            val userId = add { required<String>("user_id") }
            val scheduleType = add { required<Int>("schedule_type", 1) }
            val startTime = add { required<Long>("start_time") }
            val description = add { nullable<String>("description") }

            // You can also access external dependencies here that are present
            // as instance variables in the enclosing class.
            // For example, you can access a database connection or a logger.

            // This handles the event after it's been fully deserialized and populated with dynamic properties
            on { event ->
                // Access regular properties
                println("Event: ${event.name}")
                println("Timestamp: ${event.timestamp}")

                // Access dynamic properties
                println("User ID: ${event.context[userId]}")
                println("ScheduleType: ${event.context[scheduleType]}")
                println("Start Time: ${event.context[startTime]}")
                println("Description: ${event.context[description]}")
            }
        })
    webhookHandler.onError { request, throwable ->
        println("Error handling request: $request, error: $throwable")
    }
}
