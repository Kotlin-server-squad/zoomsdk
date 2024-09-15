package com.kss.zoom.examples.webhooks

import com.kss.zoom.model.context.DynamicProperty.Companion.required
import com.kss.zoom.model.context.withSerializer
import com.kss.zoom.webhooks.DefaultWebhookHandler.Companion.webhook
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface MeetingParticipant {
    val userName: String
    val email: String
}

@Serializable
data class ParticipantAdmitted(
    @SerialName("user_name") override val userName: String,
    override val email: String,
    @SerialName("date_time") val joinTime: Instant,
    @SerialName("leave_reason") val leaveReason: String? = null,
) : MeetingParticipant

@Serializable
data class ParticipantLeft(
    @SerialName("user_name") override val userName: String,
    override val email: String,
    @SerialName("leave_time") val leaveTime: Instant,
    @SerialName("leave_reason") val leaveReason: String,
) : MeetingParticipant

fun main() = withLogger("MultipleEvents") { logger ->
    val webhookHandler = webhook {
        onError { webhookRequest, throwable ->
            logger.error("Error handling webhook request: $webhookRequest", throwable)
        }
        handler("meeting.participant_joined") {
            val participant = add<ParticipantAdmitted> { required("participant") }
                .withSerializer(ParticipantAdmitted.serializer())

            on { event ->
                logger.info("Event: $event")
                logger.info("Received event {} at {}", event.name, event.timestamp)
                logger.info("User Name: {}", event.context[participant].userName)
                logger.info("Email: {}", event.context[participant].email)
                logger.info("Join Time: {}", event.context[participant].joinTime)
            }
        }
        handler("meeting.participant_left") {
            val participant = add<ParticipantLeft> { required("participant") }
                .withSerializer(ParticipantLeft.serializer())

            on { event ->
                logger.info("Event: $event")
                logger.info("Received event {} at {}", event.name, event.timestamp)
                logger.info("User Name: {}", event.context[participant].userName)
                logger.info("Email: {}", event.context[participant].email)
                logger.info("Leave Time: {}", event.context[participant].leaveTime)
                logger.info("Leave Reason: {}", event.context[participant].leaveReason)
            }
        }
    }

    // Simulate a participant joined event.
    logger.info("Simulating participant joined meeting event.")
    webhookHandler.handle(
        """
            {
              "event": "meeting.participant_joined",
              "event_ts": 1626230691572,
              "payload": {
                "account_id": "AAAAAABBBB",
                "object": {
                  "id": "1234567890",
                  "uuid": "4444AAAiAAAAAiAiAiiAii==",
                  "host_id": "x1yCzABCDEfg23HiJKl4mN",
                  "topic": "My Meeting",
                  "type": 8,
                  "start_time": "2021-07-13T21:44:51Z",
                  "timezone": "America/Los_Angeles",
                  "duration": 60,
                  "participant": {
                    "user_id": "1234567890",
                    "user_name": "Jill Chill",
                    "id": "iFxeBPYun6SAiWUzBcEkX",
                    "participant_uuid": "55555AAAiAAAAAiAiAiiAii",
                    "date_time": "2021-07-13T21:44:51Z",
                    "email": "jchill@example.com",
                    "registrant_id": "abcdefghij0-klmnopq23456",
                    "participant_user_id": "rstuvwxyza789-cde",
                    "customer_key": "349589LkJyeW",
                    "phone_number": "8615250064084"
                  }
                }
              }
            }        
        """.trimIndent()
    )

    // Simulate a participant left event.
    logger.info("Simulating participant left meeting event.")
    webhookHandler.handle(
        """
            {
              "event": "meeting.participant_left",
              "event_ts": 1626230691572,
              "payload": {
                "account_id": "AAAAAABBBB",
                "object": {
                  "id": "1234567890",
                  "uuid": "4444AAAiAAAAAiAiAiiAii==",
                  "host_id": "x1yCzABCDEfg23HiJKl4mN",
                  "topic": "My Meeting",
                  "type": 8,
                  "start_time": "2021-07-13T21:44:51Z",
                  "timezone": "America/Los_Angeles",
                  "duration": 60,
                  "participant": {
                    "user_id": "1234567890",
                    "user_name": "Jill Chill",
                    "id": "iFxeBPYun6SAiWUzBcEkX",
                    "participant_uuid": "55555AAAiAAAAAiAiAiiAii",
                    "leave_time": "2021-07-13T22:50:51Z",
                    "leave_reason": "Jill Chill left the meeting.<br>Reason: Host ended the meeting.",
                    "date_time": "2021-07-13T21:44:51Z",
                    "email": "jchill@example.com",
                    "registrant_id": "abcdefghij0-klmnopq23456",
                    "participant_user_id": "rstuvwxyza789-cde",
                    "customer_key": "349589LkJyeW",
                    "phone_number": "8615250064084"
                  }
                }
              }
            }        
        """.trimIndent()
    )
}
