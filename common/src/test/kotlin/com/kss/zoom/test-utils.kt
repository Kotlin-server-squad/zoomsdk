package com.kss.zoom

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows

fun <T> verifyFailure(expectedCode: Int, expectedMessage: String, block: suspend () -> Result<T>) {
    val exception = assertThrows(ZoomException::class.java) {
        runBlocking {
            block().getOrThrow()

        }
    }
    assertEquals(expectedCode, exception.code)
    assertEquals(expectedMessage, exception.message)
}