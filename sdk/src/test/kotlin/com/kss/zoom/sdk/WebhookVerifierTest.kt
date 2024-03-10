package com.kss.zoom.sdk

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.utils.io.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebhookVerifierTest {

    companion object {
        private const val DEFAULT_SIGNATURE_HEADER_NAME = "x-zm-signature"
        private const val DEFAULT_TIMESTAMP_HEADER_NAME = "x-zm-request-timestamp"
        private const val VERIFICATION_TOKEN = "test-token"
        private const val TIMESTAMP = 123456789L
        private val PAYLOAD = """
            { "key": "value" }
        """.trimIndent()
    }

    private val verifier = verifier()

    @Test
    fun `should accept valid request`() = runBlocking {
        val call = call(signature = validSignature(), timestamp = TIMESTAMP)
        assertTrue(verifier.verify(call), "Request should be valid")
    }

    @Test
    fun `should reject request with invalid signature`() = runBlocking {
        val invalidCall = call(signature = "invalid-signature", timestamp = TIMESTAMP)
        assertFalse(verifier.verify(invalidCall), "Request should be invalid")
    }

    @Test
    fun `should reject request with missing signature`() = runBlocking {
        val invalidCall = call(timestamp = TIMESTAMP)
        assertFalse(verifier.verify(invalidCall), "Request should be invalid")
    }

    @Test
    fun `should reject request with missing timestamp`() = runBlocking {
        val invalidCall = call(signature = validSignature())
        assertFalse(verifier.verify(invalidCall), "Request should be invalid")
    }

    @Test
    fun `should allow for custom signature header`() = runBlocking {
        val customVerifier = verifier(signatureHeaderName = "custom-signature")
        val call = call(signatureHeaderName = "custom-signature", signature = validSignature(), timestamp = TIMESTAMP)
        assertTrue(customVerifier.verify(call), "Request should be valid")
    }

    @Test
    fun `should allow for custom timestamp header`() = runBlocking {
        val customVerifier = verifier(timestampHeaderName = "custom-timestamp")
        val call = call(timestampHeaderName = "custom-timestamp", signature = validSignature(), timestamp = TIMESTAMP)
        assertTrue(customVerifier.verify(call), "Request should be valid")
    }

    private fun validSignature(): String {
        val message = "v0:$TIMESTAMP:$PAYLOAD"
        return "v0=" + HmacUtils(HmacAlgorithms.HMAC_SHA_256, VERIFICATION_TOKEN).hmacHex(message)
    }

    private fun verifier(
        signatureHeaderName: String = DEFAULT_SIGNATURE_HEADER_NAME,
        timestampHeaderName: String = DEFAULT_TIMESTAMP_HEADER_NAME
    ): WebhookVerifier {
        return WebhookVerifierImpl(
            verificationToken = VERIFICATION_TOKEN,
            signatureHeaderName = signatureHeaderName,
            timestampHeaderName = timestampHeaderName
        )
    }

    private fun call(
        signatureHeaderName: String = DEFAULT_SIGNATURE_HEADER_NAME,
        timestampHeaderName: String = DEFAULT_TIMESTAMP_HEADER_NAME,
        signature: String? = null,
        timestamp: Long? = null
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
        val channel = ByteReadChannel(PAYLOAD.toByteArray(Charsets.UTF_8))
        every { request.receiveChannel() } returns channel

        val call = mockk<ApplicationCall>()
        every { call.request } returns request

        return call
    }
}