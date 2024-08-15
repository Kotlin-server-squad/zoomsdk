package com.kss.zoom.common

import com.kss.zoom.model.CallResult

fun<T> call(block: suspend () -> CallResult<T>): T {
    TODO()
}

expect fun currentTimeMillis(): Long
