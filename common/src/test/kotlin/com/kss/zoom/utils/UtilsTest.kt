package com.kss.zoom.utils

import com.kss.zoom.ZoomException
import com.kss.zoom.verifyFailure
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.ExecutionException
import kotlin.test.Test

class UtilsTest {

    @Test
    fun `call should return result of block`() = runBlocking {
        assertEquals(1, call { Result.success(1) }, "`call` should return result of block")
    }

    @Test
    fun `call should throw an exception if the block fails`() {
        val zoomException = ZoomException(500, "Server error")
        val exception = assertThrows(ZoomException::class.java) {
            runBlocking {
                call<Int> {
                    Result.failure(zoomException)
                }
            }
        }
        verifyFailure(zoomException.code, zoomException.message, exception)
    }

    @Test
    fun `callSync should return result of block`() {
        assertEquals(1, callSync { Result.success(1) }, "`runSync` should return result of block")
    }

    @Test
    fun `callSync should throw an exception if the block fails`() {
        val zoomException = ZoomException(500, "Server error")
        val exception = assertThrows(ZoomException::class.java) {
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
        val exception = assertThrows(ExecutionException::class.java) {
            future.get()
        }
        assertTrue(exception.cause is ZoomException, "Expected exception cause is not ZoomException")
        verifyFailure(zoomException.code, zoomException.message, exception.cause as ZoomException)
    }
}