package com.kss.zoom.sdk.common

import com.kss.zoom.assertThrows
import com.kss.zoom.auth.model.ZoomException
import com.kss.zoom.verifyFailure
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.ExecutionException
import kotlin.test.Test
import kotlin.test.assertTrue

class UtilsJvmTest {

    @Test
    fun `callSync should return result of block`() {
        assertEquals(1, callSync { Result.success(1) }, "`runSync` should return result of block")
    }

    @Test
    fun `callSync should throw an exception if the block fails`() {
        val zoomException = ZoomException(500, "Server error")
        val exception = assertThrows(ZoomException::class) {
            callSync<Int> {
                Result.failure(zoomException)
            }
        }
        verifyFailure(zoomException.code, zoomException.message, exception)
    }

    @Test
    fun `callAsync should return result of block as a completable future`() {
        val future = callAsync { Result.success(1) }
        assertEquals(1, future.get(), "`future` should return result of block")
    }

    @Test
    fun `callAsync should throw an exception if the block fails`() {
        val zoomException = ZoomException(500, "Server error")
        val future = callAsync { Result.failure<Int>(zoomException) }
        val exception = assertThrows(ExecutionException::class) {
            future.get()
        }
        assertTrue(exception.cause is ZoomException, "Expected exception cause is not ZoomException")
        verifyFailure(zoomException.code, zoomException.message, exception.cause as ZoomException)
    }
}