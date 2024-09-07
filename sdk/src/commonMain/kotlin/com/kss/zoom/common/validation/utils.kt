package com.kss.zoom.common.validation

import com.kss.zoom.model.validation.ValidationResult
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> call(block: suspend () -> ValidationResult<T>): T {
    return when (val result = tryCall(block)) {
        is ValidationResult.Success -> result.data
        is ValidationResult.Error -> throw IllegalStateException("Validation failed: ${result.message}")
    }
}

suspend fun <T> tryCall(block: suspend  () -> ValidationResult<T>): ValidationResult<T> {
    return try {
        block()
    } catch (e: CancellationException) {
        // Respect cancellation
        throw e
    } catch (t: Throwable) {
        ValidationResult.Error(t.message ?: "Unknown error")
    }
}
