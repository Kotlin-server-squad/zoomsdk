package com.kss.zoom.sdk.common

import com.kss.zoom.assertThrows
import com.kss.zoom.auth.model.ZoomException
import com.kss.zoom.verifyFailure
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {
    @Test
    fun callShouldReturnResultOfBlock() = runTest {
        assertEquals(1, call { Result.success(1) }, "`call` should return result of block")
    }

    @Test
    fun callShouldThrowAnExceptionIfTheBlockFails() = runTest {
        val zoomException = ZoomException(500, "Server error")
        val exception = assertThrows(ZoomException::class) {
            call { Result.failure(zoomException) }
        }
        verifyFailure(zoomException.code, zoomException.message, exception)
    }
}