package com.kss.zoom.sdk

import com.kss.zoom.sdk.ZoomMock.resetHttpClient
import com.kss.zoom.sdk.model.ZoomModule
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.slf4j.MDC

abstract class ModuleTest<M : ZoomModule> {

    abstract fun module(): M

    abstract suspend fun sdkCall(module: M): Any

    @AfterEach
    fun tearDown() {
        resetHttpClient()
    }

    @Test
    fun `should correctly set and reset correlation id`() {
        assertNull(MDC.get("correlationId"))
        val correlationId = "my-correlation-id"
        runBlocking {
            module().withCorrelationId(correlationId) {
                assert(MDC.get("correlationId") == correlationId)
            }
        }
        assertNull(MDC.get("correlationId"))
    }

    @Test
    fun `should propagate correlation id to http request`() {
        val correlationId = "my-correlation-id"
        runBlocking {
            module().withCorrelationId(correlationId) {
                sdkCall(this)
                assert(ZoomMock.lastRequest()?.headers?.get("X-Correlation-Id") == correlationId)
            }
        }
    }
}