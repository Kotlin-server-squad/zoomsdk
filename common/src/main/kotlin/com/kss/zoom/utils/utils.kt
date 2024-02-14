package com.kss.zoom.utils

suspend fun <T> call(block: suspend () -> Result<T>): T =
    block().getOrThrow()