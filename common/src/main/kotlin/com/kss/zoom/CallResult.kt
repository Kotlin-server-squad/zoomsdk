package com.kss.zoom

import com.kss.zoom.CallResult.Failure.*
import com.kss.zoom.CallResult.Success
import com.kss.zoom.ZoomException.*

sealed interface CallResult<out T> {
    data class Success<T>(val value: T) : CallResult<T>
    sealed interface Failure : CallResult<Nothing> {
        val message: String

        data class BadRequest(override val message: String) : Failure
        data class Unauthorized(override val message: String) : Failure
        data class NotFound(override val message: String) : Failure
        data class TooManyRequests(override val message: String) : Failure
        data class Error(override val message: String) : Failure
    }
}

suspend fun <T> call(call: suspend () -> CallResult<T>): T {
    return when (val result = call()) {
        is Success -> result.value
        is BadRequest -> throw RequestFailedException(result.message)
        is Unauthorized -> throw AuthorizationException(result.message)
        is NotFound -> throw ResourceNotFoundException(result.message)
        is TooManyRequests -> throw RequestFailedException(result.message)
        is Error -> throw RequestFailedException(result.message)
    }
}