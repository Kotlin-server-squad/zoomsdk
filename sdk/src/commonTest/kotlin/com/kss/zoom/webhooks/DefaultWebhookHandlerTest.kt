package com.kss.zoom.webhooks

import com.kss.zoom.common.event.DefaultEventHandler.Companion.handler
import com.kss.zoom.common.event.DynamicEventHandler
import com.kss.zoom.model.context.DynamicProperty.Companion.required
import com.kss.zoom.model.context.withSerializer
import com.kss.zoom.model.event.Event
import com.kss.zoom.model.request.WebhookRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

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
    fun `should use default value for missing properties`() = runTest {
        handleWebhook(
            eventType = "meeting.ended",
            payload = """
                {
                  "event": "meeting.ended",
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
                      "start_time": "2023-04-01T09:00:00Z",
                      "duration": 60,
                      "timezone": "America/Los_Angeles",
                      "end_time": "2023-04-01T10:00:00Z",
                      "agenda": "Discussing the product launch"
                    }
                  }
                }   
            """.trimIndent(),
            handler {
                val uuid = add { required<String>("uuid") }
                val type = add { required<Int>("type", 1) }

                on { event ->
                    assertEquals("meeting.ended", event.name, "Invalid event name")
                    assertEquals(1658940994914, event.timestamp, "Invalid timestamp")
                    assertEquals("Sdghwi7erUGDy7sud", event.context[uuid], "Invalid uuid")
                    assertEquals(
                        type.default(),
                        event.context[type],
                        "Meeting type should default to ${type.default()}"
                    )
                    eventQueue.add(event)
                }
            }
        )
        // check that the event was handled
        verifySuccess()
    }

    @Test
    fun `should fail if property value doesn't match the expected data type`() = runTest {
        handleWebhook(
            eventType = "meeting.ended",
            payload = """
                {
                  "event": "meeting.ended",
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
                      "start_time": "2023-04-01T09:00:00Z",
                      "duration": "sixty",
                      "timezone": "America/Los_Angeles",
                      "end_time": "2023-04-01T10:00:00Z",
                      "agenda": "Discussing the product launch"
                    }
                  }
                }   
            """.trimIndent(),
            handler {
                val uuid = add { required<String>("uuid") }
                val duration = add { required<Int>("duration", 0) }

                on { event ->
                    assertEquals("meeting.ended", event.name, "Invalid event name")
                    assertEquals(1658940994914, event.timestamp, "Invalid timestamp")
                    assertEquals("Sdghwi7erUGDy7sud", event.context[uuid], "Invalid uuid")
                    assertEquals(
                        duration.default(),
                        event.context[duration],
                        "Meeting duration should default to ${duration.default()}"
                    )
                    eventQueue.add(event)
                }
            }
        )
        // check that the event failed
        verifyFailure()
    }

    @Test
    fun `should fail if required property is missing and there's no default value`() = runTest {
        handleWebhook(
            eventType = "meeting.ended",
            payload = """
                {
                  "event": "meeting.ended",
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
                      "start_time": "2023-04-01T09:00:00Z",
                      "duration": "sixty",
                      "timezone": "America/Los_Angeles",
                      "end_time": "2023-04-01T10:00:00Z",
                      "agenda": "Discussing the product launch"
                    }
                  }
                }   
            """.trimIndent(),
            handler {
                val duration = add { required<Int>("duration") }

                on { event ->
                    fail("Event with invalid duration should not be handled. Unexpected duration: ${event.context[duration]}")
                }
            }
        )
        // check that the event was handled
        verifyFailure()
    }

    @Test
    fun `should handle custom data structures`() = runTest {
        handleWebhook(
            eventType = "recording.completed",
            payload = """
                {
                  "event": "recording.completed",
                  "event_ts": 1658940994914,
                  "payload": {
                    "account_id": "d8u239ur932u39u2",
                    "object": {
                      "uuid": "Sdghwi7erUGDy7sud",
                      "id": "123456789",
                      "account_id": "d8u239ur932u39u2",
                      "host_id": "js78su3jsj28su38",
                      "topic": "My Meeting",
                      "type": 2,
                      "start_time": "2023-04-01T09:00:00Z",
                      "timezone": "America/Los_Angeles",
                      "host_email": "email@example.com",
                      "duration": 60,
                      "total_size": 104857600,
                      "recording_count": 3,
                      "share_url": "https://zoom.us/recording/share/xxxx",
                      "recording_files": [
                        {
                          "id": "34fg54ygf34",
                          "meeting_id": "Sdghwi7erUGDy7sud",
                          "recording_start": "2023-04-01T09:00:00Z",
                          "recording_end": "2023-04-01T10:00:00Z",
                          "file_type": "MP4",
                          "file_size": 52428800,
                          "play_url": "https://zoom.us/recording/play/xxxx",
                          "download_url": "https://zoom.us/recording/download/xxxx",
                          "status": "completed",
                          "recording_type": "shared_screen_with_speaker_view"
                        },
                        {
                          "id": "56hkj56hkj56",
                          "meeting_id": "Sdghwi7erUGDy7sud",
                          "recording_start": "2023-04-01T09:00:00Z",
                          "recording_end": "2023-04-01T10:00:00Z",
                          "file_type": "M4A",
                          "file_size": 20971520,
                          "play_url": "https://zoom.us/recording/play/yyyy",
                          "download_url": "https://zoom.us/recording/download/yyyy",
                          "status": "completed",
                          "recording_type": "audio_only"
                        },
                        {
                          "id": "789opk789opk",
                          "meeting_id": "Sdghwi7erUGDy7sud",
                          "recording_start": "2023-04-01T09:00:00Z",
                          "recording_end": "2023-04-01T10:00:00Z",
                          "file_type": "CHAT",
                          "file_size": 102400,
                          "play_url": "https://zoom.us/recording/play/zzzz",
                          "download_url": "https://zoom.us/recording/download/zzzz",
                          "status": "completed",
                          "recording_type": "chat_file"
                        }
                      ]
                    }
                  }
                }              
            """.trimIndent(),
            handler {
                val recordings = add { required<List<Recording>>("recording_files") }
                    .withSerializer(ListSerializer(Recording.serializer()))

                on { event ->
                    assertEquals("recording.completed", event.name, "Invalid event name")
                    assertEquals(1658940994914, event.timestamp, "Invalid timestamp")
                    assertEquals(
                        listOf(
                            Recording(
                                fileType = "MP4",
                                downloadUrl = "https://zoom.us/recording/download/xxxx",
                                start = "2023-04-01T09:00:00Z",
                                end = "2023-04-01T10:00:00Z",
                            ),
                            Recording(
                                fileType = "M4A",
                                downloadUrl = "https://zoom.us/recording/download/yyyy",
                                start = "2023-04-01T09:00:00Z",
                                end = "2023-04-01T10:00:00Z",
                            ),
                            Recording(
                                fileType = "CHAT",
                                downloadUrl = "https://zoom.us/recording/download/zzzz",
                                start = "2023-04-01T09:00:00Z",
                                end = "2023-04-01T10:00:00Z",
                            ),
                        ), event.context[recordings], "Invalid recording files"
                    )
                    eventQueue.add(event)
                }
            }
        )
        // check that the event was handled
        verifySuccess()
    }

    private suspend fun handleWebhook(
        eventType: String,
        payload: String,
        eventHandler: DynamicEventHandler,
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

    private fun verifyFailure() {
        assertEquals(0, eventQueue.size)
        assertEquals(1, dlq.size)
    }
}

// A custom data class in the client code
@Serializable
data class Recording(
    @SerialName("file_type") val fileType: String,
    @SerialName("download_url") val downloadUrl: String,
    @SerialName("recording_start") val start: String,
    @SerialName("recording_end") val end: String,
)



