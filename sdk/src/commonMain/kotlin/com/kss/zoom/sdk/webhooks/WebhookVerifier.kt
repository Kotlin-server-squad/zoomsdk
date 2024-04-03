package com.kss.zoom.sdk.webhooks

import com.kss.zoom.sdk.webhooks.model.Request
import io.github.oshai.kotlinlogging.KotlinLogging

class WebhookVerifier(
    private val verificationToken: String,
    private val signatureHeaderName: String? = null,
    private val timestampHeaderName: String? = null
) : IWebhookVerifier {
    companion object {
        const val PREFIX = "v0"
        const val DEFAULT_SIGNATURE_HEADER_NAME = "x-zm-signature"
        const val DEFAULT_TIMESTAMP_HEADER_NAME = "x-zm-request-timestamp"
    }

    private val logger = KotlinLogging.logger {}

    override suspend fun verify(request: Request): Result<String> {
        return verify(request.payload, request.timestamp, request.signature)
    }

    override suspend fun verify(payload: String, timestamp: Long?, signature: String?): Result<String> {
        if (signature == null) {
            logger.debug { "No signature found in request" }
            return Result.failure(IllegalArgumentException("No signature found in request"))
        }
        if (timestamp == null) {
            logger.debug { "No timestamp found in request" }
            return Result.failure(IllegalArgumentException("No timestamp found in request"))
        }
        val message = "$PREFIX:$timestamp:$payload"
        val hashedMessage = message.hash(verificationToken)

        if (signature != "$PREFIX=$hashedMessage") {
            logger.debug { "Signature does not match" }
            return Result.failure(IllegalArgumentException("Signature does not match"))
        }

        return Result.success(payload)
    }
}