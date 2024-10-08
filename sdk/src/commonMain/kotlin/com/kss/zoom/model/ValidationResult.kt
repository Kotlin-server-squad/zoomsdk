package com.kss.zoom.model

sealed interface ValidationResult<out T> {
    data class Success<T>(val data: T) : ValidationResult<T>
    data class Error(val message: String) : ValidationResult<Nothing>
}
