package com.kss.zoom.sdk.meetings

import com.kss.zoom.sdk.webhooks.WebhookVerifier
import com.kss.zoom.sdk.webhooks.model.Request
import com.kss.zoom.sdk.webhooks.model.api.MeetingCreatedEvent
import io.ktor.server.application.*
import io.ktor.server.request.*

suspend fun IMeetings.onMeetingCreated(
    call: ApplicationCall,
    signatureHeaderName: String? = null,
    timestampHeaderName: String? = null,
    action: (MeetingCreatedEvent) -> Unit
): Result<Unit> {
    val signature = call.request.header(signatureHeaderName ?: WebhookVerifier.DEFAULT_SIGNATURE_HEADER_NAME)
    val timestamp = call.request.header(timestampHeaderName ?: WebhookVerifier.DEFAULT_TIMESTAMP_HEADER_NAME)
    val payload = call.request
        .receiveChannel()
        .readRemaining()
        .readText()
    return this.onMeetingCreated(
        Request(
            payload = payload,
            timestamp = timestamp?.toLong(),
            signature = signature
        ), action
    )
}

