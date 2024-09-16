package com.kss.zoom.examples.webhooks

import com.kss.zoom.common.jvm.serialization.InstantSerializer
import com.kss.zoom.model.context.DynamicProperty.Companion.nullable
import com.kss.zoom.model.context.DynamicProperty.Companion.required
import com.kss.zoom.model.context.withSerializer
import com.kss.zoom.webhooks.DefaultWebhookHandler.Companion.webhook

fun main() = withLogger("SingleEvent1") { logger ->
    webhook {
        onError { request, throwable ->
            logger.error("Error handling webhook request: $request", throwable)
        }
        handler("meeting.ended") {
            val uuid = add { required<String>("uuid") }
            val hostId = add { required<String>("host_id") }
            val topic = add { required<String>("topic") }
            val type = add { required<Int>("type") }
            val startTime = add { required<java.time.Instant>("start_time") }
                .withSerializer(InstantSerializer)
            val endTime = add { required<java.time.Instant>("end_time") }
                .withSerializer(InstantSerializer)
            val duration = add { required<Int>("duration") }
            val timezone = add { nullable<String?>("timezone") }
            on { event ->
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
    }.handle(
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
}
