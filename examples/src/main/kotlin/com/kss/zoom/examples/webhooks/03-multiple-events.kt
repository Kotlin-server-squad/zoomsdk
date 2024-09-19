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

@Serializable
data class WebinarRegistrant(
    val id: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val email: String,
    @SerialName("purchasing_time_frame") val timeFrame: String,
    @SerialName("role_in_purchase_process") val role: String,
    @SerialName("custom_questions") val questionnaire: List<Questionnaire>,
) {
    @Serializable
    data class Questionnaire(
        val title: String,
        val value: String,
    )
}

fun main() = withLogger("MultipleEvents") { logger ->
    val webhookHandler = webhook {
        onError { webhookRequest, exception ->
            logger.error("Error handling webhook request: $webhookRequest", exception)
        }
        handler("meeting.participant_joined") {
            val participant = add { required<ParticipantAdmitted>("participant") }
                .withSerializer(ParticipantAdmitted.serializer())

            on { event ->
                logger.info("User Name: {}", event.context[participant].userName)
                logger.info("Email: {}", event.context[participant].email)
                logger.info("Join Time: {}", event.context[participant].joinTime)
            }
        }
        handler("meeting.participant_left") {
            val participant = add { required<ParticipantLeft>("participant") }
                .withSerializer(ParticipantLeft.serializer())

            on { event ->
                logger.info("User Name: {}", event.context[participant].userName)
                logger.info("Email: {}", event.context[participant].email)
                logger.info("Leave Time: {}", event.context[participant].leaveTime)
                logger.info("Leave Reason: {}", event.context[participant].leaveReason)
            }
        }
        handler("webinar.registration_created") {
            val registrant = add { required<WebinarRegistrant>("registrant") }
                .withSerializer(WebinarRegistrant.serializer())

            on { event ->
                logger.info("ID: {}", event.context[registrant].id)
                logger.info("Email: {}", event.context[registrant].email)
                logger.info("Role: {}", event.context[registrant].role)
                logger.info("First Name: {}", event.context[registrant].firstName)
                logger.info("Last Name: {}", event.context[registrant].lastName)
                logger.info("Questionnaire:\n{}", event.context[registrant].questionnaire.joinToString { "  Q: ${it.title}\n  A: ${it.value}\n" })
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

    // Simulate a webinar registration created event.
    logger.info("Simulating webinar registration created event.")
    webhookHandler.handle(
        """
            {
              "event": "webinar.registration_created",
              "event_ts": 1626230691572,
              "payload": {
                "account_id": "AAAAAABBBB",
                "object": {
                  "uuid": "4444AAAiAAAAAiAiAiiAii==",
                  "id": 1234567890,
                  "host_id": "x1yCzABCDEfg23HiJKl4mN",
                  "topic": "My Webinar",
                  "type": 9,
                  "start_time": "2021-07-13T21:44:51Z",
                  "duration": 60,
                  "timezone": "America/Los_Angeles",
                  "occurrences": [
                    {
                      "occurrence_id": "ABCDE12345",
                      "start_time": "2021-07-13T21:44:51Z"
                    }
                  ],
                  "registrant": {
                    "id": "iFxeBPYun6SAiWUzBcEkX",
                    "first_name": "Jill",
                    "last_name": "Chill",
                    "email": "jchill@example.com",
                    "address": "1800 Amphibious Blvd.",
                    "city": "Mountain View",
                    "country": "US",
                    "zip": "94045",
                    "state": "CA",
                    "phone": "5550100",
                    "industry": "Food",
                    "org": "Cooking Org",
                    "job_title": "Chef",
                    "purchasing_time_frame": "1-3 months",
                    "role_in_purchase_process": "Influencer",
                    "no_of_employees": "1-20",
                    "comments": "Looking forward to the Webinar",
                    "custom_questions": [
                      {
                        "title": "What do you hope to learn from this Webinar?",
                        "value": "Look forward to learning how you come up with new recipes and what other services you offer."
                      }
                    ],
                    "status": "approved",
                    "join_url": "https://example.com",
                    "tracking_source": {
                      "id": "5516482804110",
                      "source_name": "general",
                      "tracking_url": "https://example.com/webinar/register/5516482804110/WN_juM2BGyLQMyQ_ZrqiGRhLg"
                    }
                  }
                }
              }
            }            
        """.trimIndent()
    )
}
