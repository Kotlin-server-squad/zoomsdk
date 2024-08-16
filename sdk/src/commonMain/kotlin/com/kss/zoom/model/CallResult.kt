package com.kss.zoom.model

sealed interface CallResult<out T> {
    data class Success<T>(val data: T) : CallResult<T>
    data class Error(val message: String) : CallResult<Nothing>
    data object NotFound : CallResult<Nothing>
}

