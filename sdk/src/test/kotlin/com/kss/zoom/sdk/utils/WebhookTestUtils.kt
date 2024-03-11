package com.kss.zoom.sdk.utils

import com.kss.zoom.sdk.WebhookVerifier
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.utils.io.*
import io.mockk.every
import io.mockk.mockk
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils

object WebhookTestUtils {
    private const val DEFAULT_SIGNATURE_HEADER_NAME = "x-zm-signature"
    private const val DEFAULT_TIMESTAMP_HEADER_NAME = "x-zm-request-timestamp"
    private const val VERIFICATION_TOKEN = "test-token"
    const val TIMESTAMP = 123456789L
    private val PAYLOAD = """
            { "key": "value" }
        """.trimIndent()

    fun verifier(
        signatureHeaderName: String = DEFAULT_SIGNATURE_HEADER_NAME,
        timestampHeaderName: String = DEFAULT_TIMESTAMP_HEADER_NAME
    ): WebhookVerifier {
        return WebhookVerifier.create(
            verificationToken = VERIFICATION_TOKEN,
            signatureHeaderName = signatureHeaderName,
            timestampHeaderName = timestampHeaderName
        )
    }

    fun applicationCall(
        signatureHeaderName: String = DEFAULT_SIGNATURE_HEADER_NAME,
        timestampHeaderName: String = DEFAULT_TIMESTAMP_HEADER_NAME,
        payload: String = PAYLOAD,
        signature: String? = null,
        timestamp: Long? = TIMESTAMP
    ): ApplicationCall {
        val request = mockk<ApplicationRequest>()
        val headers = mutableMapOf<String, List<String>>()
        if (signature != null) {
            headers[signatureHeaderName] = listOf(signature)
        }
        if (timestamp != null) {
            headers[timestampHeaderName] = listOf(timestamp.toString())
        }

        every { request.headers } answers {
            Headers.build { headers.forEach { (name, values) -> appendAll(name, values) } }
        }
        val channel = ByteReadChannel(payload.toByteArray(Charsets.UTF_8))
        every { request.receiveChannel() } returns channel

        val call = mockk<ApplicationCall>()
        every { call.request } returns request

        return call
    }

    fun validSignature(payload: String = PAYLOAD): String {
        val message = "v0:$TIMESTAMP:$payload"
        return "v0=" + HmacUtils(HmacAlgorithms.HMAC_SHA_256, VERIFICATION_TOKEN).hmacHex(message)
    }
}