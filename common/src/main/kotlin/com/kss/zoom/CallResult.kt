package com.kss.zoom

import com.kss.zoom.CallResult.Failure.*
import com.kss.zoom.CallResult.Success

sealed interface CallResult<out T> {
    data class Success<T>(val value: T) : CallResult<T>
    sealed interface Failure : CallResult<Nothing> {

        data class BadRequest(val message: String) : Failure
        data object Unauthorized : Failure
        data object NotFound : Failure
        data object TooManyRequests : Failure
        data class Error(val message: String) : Failure
    }
}

suspend fun <T> call(call: suspend () -> CallResult<T>): T {
    return when (val result = call()) {
        is Success -> result.value
        is BadRequest -> throw IllegalArgumentException(result.message)
        is Unauthorized -> throw IllegalStateException("You're not authorized to perform this action")
        is NotFound -> throw IllegalStateException("Resource not found")
        is TooManyRequests -> throw IllegalStateException("Too many requests")
        is Error -> throw IllegalStateException(result.message)
    }
}