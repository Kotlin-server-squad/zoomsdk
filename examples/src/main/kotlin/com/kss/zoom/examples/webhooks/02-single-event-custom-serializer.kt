package com.kss.zoom.examples.webhooks

import com.kss.zoom.model.context.DynamicProperty.Companion.required
import com.kss.zoom.model.context.withSerializer
import com.kss.zoom.webhooks.DefaultWebhookHandler.Companion.webhook
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveStreaming(
    val service: Service,
    @SerialName("custom_live_streaming_settings") val customSettings: CustomSettings,
    @SerialName("date_time") val dateTime: String
) {
    enum class Service(val value: String) {
        Facebook("Facebook"),
        Workplace_by_Facebook("Workplace by Facebook"),
        YouTube("YouTube"),
        Twitch("Twitch"),
        Custom_Live_Streaming_Service("Custom")
    }

    @Serializable
    data class CustomSettings(
        @SerialName("stream_url") val streamUrl: String,
        @SerialName("stream_key") val streamKey: String,
        @SerialName("page_url") val pageUrl: String,
        val resolution: String?,
    )
}

fun main() = withLogger("SingleEvent2") { logger ->
    webhook {
        onError { webhookRequest, throwable ->
            logger.error("Error handling webhook request: $webhookRequest", throwable)
        }
        handler("meeting.live_streaming_started") {
            val uuid = add<String> { required("uuid") }
            val startTime = add<Instant> { required("start_time") }
            val liveStreaming = add<LiveStreaming> { required("live_streaming") }
                .withSerializer(LiveStreaming.serializer())

            on { event ->
                logger.info("Event: $event")
                logger.info("Received event {} at {}", event.name, event.timestamp)
                logger.info("UUID: {}", event.context[uuid])
                logger.info("Start Time: {}", event.context[startTime])
                logger.info("Live Streaming: {}", event.context[liveStreaming])
                logger.info("Live Streaming Service: {}", event.context[liveStreaming].service.value)
            }
        }
    }.handle(
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
