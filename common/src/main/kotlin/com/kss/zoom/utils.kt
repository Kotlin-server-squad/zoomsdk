package com.kss.zoom

suspend fun <T> call(block: suspend () -> Result<T>): T =
    block().getOrThrow()