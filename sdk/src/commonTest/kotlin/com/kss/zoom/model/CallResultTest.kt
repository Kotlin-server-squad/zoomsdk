package com.kss.zoom.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CallResultTest {

    @Test
    fun `should return the data when success`() {
        val data = "data"
        val result = CallResult.Success(data)
        assertEquals(data, result.data, "Data should be the same")
    }

    @Test
    fun `should return the expected message when bad request`() {
        assertMessage(CallResult.Error.BadRequest, "Bad request")
    }

    @Test
    fun `should return the expected message when forbidden`() {
        assertMessage(CallResult.Error.Forbidden, "Forbidden")
    }

    @Test
    fun `should return the expected message when not found`() {
        assertMessage(CallResult.Error.NotFound, "Not found")
    }

    @Test
    fun `should return the expected message when too many requests`() {
        assertMessage(CallResult.Error.TooManyRequests, "Too many requests")
    }

    @Test
    fun `should return the expected message when unauthorized`() {
        assertMessage(CallResult.Error.Unauthorized, "Unauthorized")
    }

    @Test
    fun `should return the expected message when other error`() {
        val message = "message"
        assertMessage(CallResult.Error.Other(message), message)
    }

    private fun assertMessage(result: CallResult.Error, expected: String) {
        assertEquals(expected, result.message, "Message should be the same")
    }
}
