package com.kss.zoom.examples.webhooks

import com.kss.zoom.common.event.BaseEventHandler
import com.kss.zoom.common.jvm.serialization.InstantSerializer
import com.kss.zoom.model.context.DynamicProperty.Companion.nullable
import com.kss.zoom.model.context.DynamicProperty.Companion.required
import com.kss.zoom.model.context.withSerializer
import com.kss.zoom.model.event.Event
import com.kss.zoom.webhooks.DefaultWebhookHandler
import kotlinx.datetime.Instant
import org.slf4j.LoggerFactory

/**
 * This example demonstrates how to handle multiple events using the [DefaultWebhookHandler] without using the DSL.
 * The setup lends itself well to using frameworks like Spring or Micronaut.
 */
fun main() = withLogger("NoDslWebhookHandler") { logger ->
    val webhookHandler = DefaultWebhookHandler()
    webhookHandler
        .onError { webhookRequest, throwable ->
            logger.error("Error handling webhook request: $webhookRequest", throwable)
        }
        .register("meeting.ended", MeetingEndedEventHandler())
        .register("meeting.live_streaming_started", LiveStreamingStartedEventHandler())

    // Simulate a meeting ended event.
    logger.info("Simulating meeting ended event.")
    webhookHandler.handle(
        """
            {
              "event": "meeting.ended",
              "event_ts": 1626230691572,
              "payload": {
                "account_id": "AAAAAABBBB",
                "operator": "admin@example.com",
                "operator_id": "z8yCxjabcdEFGHfp8uQ",
                "operation": "single",
                "object": {
                  "id": "1234567890",
                  "uuid": "4444AAAiAAAAAiAiAiiAii==",
                  "host_id": "x1yCzABCDEfg23HiJKl4mN",
                  "topic": "My Meeting",
                  "type": 3,
                  "start_time": "2021-07-13T21:44:51Z",
                  "timezone": "America/Los_Angeles",
                  "duration": 60,
                  "end_time": "2021-07-13T23:00:51Z"
                }
              }
            }                
        """.trimIndent()
    )

    // Simulate a live-streaming started event.
    logger.info("Simulating live-streaming meeting event.")
    webhookHandler.handle(
        """
        {
          "event": "meeting.live_streaming_started",
          "event_ts": 1627906965803,
          "payload": {
            "account_id": "D8cJuqWVQ623CI4Q8yQK0Q",
            "operator": "admin@example.com",
            "operator_id": "z8yCxjabcdEFGHfp8uQ",
            "object": {
              "id": 1234567890,
              "uuid": "4444AAAiAAAAAiAiAiiAii==",
              "host_id": "x1yCzABCDEfg23HiJKl4mN",
              "topic": "My Meeting",
              "type": 2,
              "start_time": "2021-07-13T21:44:51Z",
              "timezone": "America/Los_Angeles",
              "duration": 60,
              "live_streaming": {
                "service": "Custom_Live_Streaming_Service",
                "custom_live_streaming_settings": {
                  "stream_url": "https://example.com/livestream",
                  "stream_key": "ABCDEFG12345HIJ6789",
                  "page_url": "https://example.com/livestream/123",
                  "resolution": "1080p"
                },
                "date_time": "2021-08-02T12:22:45Z"
              }
            }
          }
        }               
        """.trimIndent()
    )
}

class MeetingEndedEventHandler : BaseEventHandler() {
    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    private val uuid = add { required<String>("uuid") }
    private val hostId = add { required<String>("host_id") }
    private val topic = add { required<String>("topic") }
    private val type =  add { required<Int>("type") }
    private val startTime = add { required<java.time.Instant>("start_time") }
        .withSerializer(InstantSerializer)
    private val endTime = add { required<java.time.Instant>("end_time") }
        .withSerializer(InstantSerializer)
    private val duration = add { required<Int>("duration") }
    private val timezone = add { nullable<String>("timezone") }

    override suspend fun on(event: Event) {
        logger.info("Event: $event")
        logger.info("Received event {} at {}", event.name, event.timestamp)
        logger.info("UUID: {}", event.context[uuid])
        logger.info("Host ID: {}", event.context[hostId])
        logger.info("Topic: {}", event.context[topic])
        logger.info("Type: {}", event.context[type])
        logger.info("Start Time: {}", event.context[startTime])
        logger.info("End Time: {}", event.context[endTime])
        logger.info("Duration: {}", event.context[duration])
        logger.info("Timezone: {}", event.context[timezone])
    }
}

class LiveStreamingStartedEventHandler : BaseEventHandler() {
    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    private val uuid = add { required<String>("uuid") }
    private val startTime = add { required<Instant>("start_time") }
    private val liveStreaming = add { required<LiveStreaming>("live_streaming") }
        .withSerializer(LiveStreaming.serializer())

    override suspend fun on(event: Event) {
        logger.info("Event: $event")
        logger.info("Received event {} at {}", event.name, event.timestamp)
        logger.info("UUID: {}", event.context[uuid])
        logger.info("Start Time: {}", event.context[startTime])
        logger.info("Live Streaming: {}", event.context[liveStreaming])
        logger.info("Live Streaming Service: {}", event.context[liveStreaming].service.value)
    }
}
