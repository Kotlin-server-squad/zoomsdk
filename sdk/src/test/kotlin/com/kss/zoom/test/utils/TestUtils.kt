package com.kss.zoom.test.utils

import com.kss.zoom.sdk.common.model.ZoomException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions

fun <T> verifyFailure(expectedCode: Int, expectedMessage: String, block: suspend () -> Result<T>) {
    val exception = Assertions.assertThrows(ZoomException::class.java) {
        runBlocking {
            block().getOrThrow()
        }
    }
    verifyFailure(expectedCode, expectedMessage, exception)
}

fun verifyFailure(expectedCode: Int, expectedMessage: String?, exception: ZoomException) {
    Assertions.assertEquals(expectedCode, exception.code)
    Assertions.assertEquals(expectedMessage, exception.message)
}