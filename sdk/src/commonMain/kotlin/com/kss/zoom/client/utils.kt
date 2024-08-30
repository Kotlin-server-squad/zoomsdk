package com.kss.zoom.client

import com.kss.zoom.common.tryCall
import com.kss.zoom.model.CallResult
import io.ktor.client.call.*
import io.ktor.client.statement.*

suspend inline fun <reified T> tryHttpCall(crossinline block: suspend () -> HttpResponse): CallResult<T> {
    return tryCall {
        val response = block()
        if (response.status.value in 200..299) {
            CallResult.Success(response.body())
        } else {
            when (response.status.value) {
                400 -> CallResult.Error.BadRequest
                401 -> CallResult.Error.Unauthorized
                403 -> CallResult.Error.Forbidden
                404 -> CallResult.Error.NotFound
                429 -> CallResult.Error.TooManyRequests
                else -> CallResult.Error.Other("Call failed with status ${response.status.value}")
            }
        }
    }
}
