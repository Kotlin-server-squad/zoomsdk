package com.kss.zoom.sdk

import com.kss.zoom.sdk.utils.WebhookTestUtils.applicationCall
import com.kss.zoom.sdk.utils.WebhookTestUtils.validSignature
import com.kss.zoom.sdk.utils.WebhookTestUtils.verifier
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class WebhookVerifierTest {

    private val verifier = verifier()

    @Test
    fun `should accept valid request`() = runBlocking {
        val call = applicationCall(signature = validSignature())
        assertTrue(verifier.verify(call).isSuccess, "Request should be valid")
    }

    @Test
    fun `should reject request with invalid signature`() = runBlocking {
        val invalidCall = applicationCall(signature = "invalid-signature")
        assertTrue(verifier.verify(invalidCall).isFailure, "Request should be invalid")
    }

    @Test
    fun `should reject request with missing signature`() = runBlocking {
        val invalidCall = applicationCall(signature = null)
        assertTrue(verifier.verify(invalidCall).isFailure, "Request should be invalid")
    }

    @Test
    fun `should reject request with missing timestamp`() = runBlocking {
        val invalidCall = applicationCall(signature = validSignature(), timestamp = null)
        assertTrue(verifier.verify(invalidCall).isFailure, "Request should be invalid")
    }

    @Test
    fun `should allow for custom signature header`() = runBlocking {
        val customVerifier = verifier(signatureHeaderName = "custom-signature")
        val call = applicationCall(signatureHeaderName = "custom-signature", signature = validSignature())
        assertTrue(customVerifier.verify(call).isSuccess, "Request should be valid")
    }

    @Test
    fun `should allow for custom timestamp header`() = runBlocking {
        val customVerifier = verifier(timestampHeaderName = "custom-timestamp")
        val call = applicationCall(timestampHeaderName = "custom-timestamp", signature = validSignature())
        assertTrue(customVerifier.verify(call).isSuccess, "Request should be valid")
    }
}