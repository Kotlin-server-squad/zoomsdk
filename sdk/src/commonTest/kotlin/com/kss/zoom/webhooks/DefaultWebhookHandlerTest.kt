package com.kss.zoom.webhooks

import com.kss.zoom.common.event.DefaultEventHandler.Companion.handler
import com.kss.zoom.common.event.EventHandler
import com.kss.zoom.model.context.DynamicProperty.Companion.required
import com.kss.zoom.model.event.Event
import com.kss.zoom.model.request.WebhookRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultWebhookHandlerTest {

    // Successfully processed events are added to the eventQueue
    private val eventQueue = mutableListOf<Event>()

    // Events that failed to process are added to the dead letter queue
    private val dlq = mutableMapOf<WebhookRequest, Throwable>()

    @AfterTest
    fun tearDown() {
        eventQueue.clear()
        dlq.clear()
    }

    @Test
    fun `should handle valid event with dynamic fields`() = runTest {
        handleWebhook(
            eventType = "meeting.started",
            payload = """
                {
                  "event": "meeting.started",
                  "event_ts": 1658940994914,
                  "payload": {
                    "account_id": "d8u239ur932u39u2",
                    "operator": "email@example.com",
                    "operator_id": "iX3c3weri9PPuiP3",
                    "object": {
                      "uuid": "Sdghwi7erUGDy7sud",
                      "id": "123456789",
                      "host_id": "js78su3jsj28su38",
                      "topic": "My Meeting",
                      "type": 2,
                      "start_time": "2023-04-01T09:00:00Z",
                      "duration": 60,
                      "timezone": "America/Los_Angeles",
                      "password": "xyz",
                      "agenda": "Discussing the product launch"
                    }
                  }
                }                
            """.trimIndent(),
            handler {
                val uuid = add { required<String>("uuid") }
                val hostId = add { required<String>("host_id") }
                val topic = add { required<String>("topic") }
                val agenda = add { required<String>("agenda") }
                val type = add { required<Int>("type") }
                val duration = add { required<Int>("duration", 0) }

                on { event ->
                    assertEquals("meeting.started", event.name, "Invalid event name")
                    assertEquals(1658940994914, event.timestamp, "Invalid timestamp")
                    assertEquals("Sdghwi7erUGDy7sud", event.context[uuid], "Invalid uuid")
                    assertEquals("js78su3jsj28su38", event.context[hostId], "Invalid hostId")
                    assertEquals("My Meeting", event.context[topic], "Invalid topic")
                    assertEquals("Discussing the product launch", event.context[agenda], "Invalid agenda")
                    assertEquals(2, event.context[type], "Invalid meeting type")
                    assertEquals(60, event.context[duration], "Invalid meeting duration")
                    eventQueue.add(event)
                }
            }
        )
        // check that the event was handled
        verifySuccess()
    }

    @Test
    fun `should use default value for missing properties`() {

    }

    @Test
    fun `should use default value for invalid properties`() {

    }

    @Test
    fun `should fail if required property is missing and there's no default value`() {

    }

    private suspend fun handleWebhook(
        eventType: String,
        payload: String,
        eventHandler: EventHandler,
    ) {
        val handler = DefaultWebhookHandler().register(eventType, eventHandler)
        handler.onError { request, throwable ->
            dlq[request] = throwable
        }
        handler.handle(WebhookRequest("signature", 1234567890, payload))
    }

    private fun verifySuccess() {
        assertEquals(1, eventQueue.size)
        assertEquals(0, dlq.size)
    }
}
