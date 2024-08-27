package com.kss.zoom.client

import com.kss.zoom.common.tryCall
import com.kss.zoom.model.CallResult
import io.ktor.client.call.*
import io.ktor.client.statement.*

suspend inline fun <reified T> tryHttpCall(crossinline block: suspend () -> HttpResponse): CallResult<T> {
    return tryCall {
        val response = block()
        if (response.status.value in 200..299) {
            println("Response BODY")
            println(response.bodyAsText())
            CallResult.Success(response.body())
        } else {
            when (response.status.value) {
                400 -> CallResult.Error("Bad request")
                401, 403 -> CallResult.Error("Unauthorized")
                404 -> CallResult.NotFound
                429 -> CallResult.Error("Too many requests")
                else -> CallResult.Error("Call failed with status ${response.status.value}")
            }
        }
    }
}
