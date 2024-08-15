package com.kss.zoom.model

sealed interface CallResult<out T> {
    data class Success<T>(val data: T) : CallResult<T>
    data class Error(val message: String) : CallResult<Nothing>
    data object NotFound : CallResult<Nothing>
}

fun <T, R> CallResult<T>.map(transform: (T) -> R): CallResult<R> = when (this) {
    is CallResult.Success -> CallResult.Success(transform(data))
    is CallResult.Error -> CallResult.Error(message)
    is CallResult.NotFound -> CallResult.NotFound
}
