package com.kss.zoom.sdk.common

fun assert(condition: Boolean, message: () -> String) {
    if (!condition) {
        throw AssertionError(message())
    }
}

suspend fun <T> call(block: suspend () -> Result<T>): T =
    block().getOrThrow()

expect suspend fun <T> withMDCContext(mdcData: Map<String, String>, block: suspend () -> T): T
