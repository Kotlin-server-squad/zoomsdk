package com.kss.zoom

import com.kss.zoom.auth.model.ZoomException
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlin.reflect.KClass
import kotlin.test.assertEquals

fun <T> verifyFailure(expectedCode: Int, expectedMessage: String, block: suspend () -> Result<T>) {
    try {
        runTest {
            block().getOrThrow()
        }
    } catch (e: ZoomException) {
        verifyFailure(expectedCode, expectedMessage, e)
    }
}

fun verifyFailure(expectedCode: Int, expectedMessage: String?, exception: ZoomException) {
    assertEquals(expectedCode, exception.code)
    assertEquals(expectedMessage, exception.message)
}

inline fun <reified E : Exception> assertThrows(targetClass: KClass<E>, block: () -> Unit): E {
    try {
        block()
    } catch (e: Exception) {
        if (targetClass.isInstance(e)) {
            return e as E
        }
        throw e
    }
    throw AssertionError("Expected exception of type ${targetClass.simpleName} but no exception was thrown")
}

fun mockEngine(handler: (HttpRequestData) -> String?): HttpClientEngine = MockEngine.create {
    this.addHandler {
        handler(it)?.let { data ->
            respond(
                content = ByteReadChannel(data),
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                ),
                status = HttpStatusCode.OK
            )
        } ?: respondError(
            HttpStatusCode.Unauthorized,
            "Unauthorized access to the resource."
        )
    }
}
