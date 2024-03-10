package com.kss.zoom.sdk

import com.kss.zoom.sdk.WebhookVerifier.Companion.PREFIX
import io.ktor.server.application.*
import io.ktor.server.request.*
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface WebhookVerifier {
    companion object {
        const val PREFIX = "v0"
        const val DEFAULT_SIGNATURE_HEADER_NAME = "x-zm-signature"
        const val DEFAULT_TIMESTAMP_HEADER_NAME = "x-zm-request-timestamp"
        fun create(verificationToken: String): WebhookVerifier = WebhookVerifierImpl(verificationToken)
    }

    suspend fun verify(call: ApplicationCall): Boolean
}

class WebhookVerifierImpl(
    private val verificationToken: String,
    private val signatureHeaderName: String? = null,
    private val timestampHeaderName: String? = null
) :
    WebhookVerifier {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    override suspend fun verify(call: ApplicationCall): Boolean {
        val signature = call.request.header(signatureHeaderName ?: WebhookVerifier.DEFAULT_SIGNATURE_HEADER_NAME)
        if (signature == null) {
            logger.debug("No signature found in request")
            return false
        }
        val timestamp = call.request.header(timestampHeaderName ?: WebhookVerifier.DEFAULT_TIMESTAMP_HEADER_NAME)
        if (timestamp == null) {
            logger.debug("No timestamp found in request")
            return false
        }
        val payload = call.request
            .receiveChannel()
            .readRemaining()
            .readText()
        val message = "$PREFIX:$timestamp:$payload"
        val hashedMessage = HmacUtils(HmacAlgorithms.HMAC_SHA_256, verificationToken).hmacHex(message)

        if (signature != "$PREFIX=$hashedMessage") {
            logger.debug("Signature does not match")
            return false
        }

        return true
    }
}
