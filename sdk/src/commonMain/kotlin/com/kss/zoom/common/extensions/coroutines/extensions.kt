package com.kss.zoom.common.extensions.coroutines

import com.kss.zoom.model.CallResult

suspend fun <T, R> CallResult<T>.map(transform: suspend (T) -> R): CallResult<R> = when (this) {
    is CallResult.Success -> CallResult.Success(transform(data))
    is CallResult.Error -> CallResult.Error(message)
    is CallResult.NotFound -> CallResult.NotFound
}

suspend fun <T, R> CallResult<T>.flatMap(transform: suspend (T) -> CallResult<R>): CallResult<R> = when (this) {
    is CallResult.Success -> transform(data)
    is CallResult.Error -> CallResult.Error(message)
    is CallResult.NotFound -> CallResult.NotFound
}
