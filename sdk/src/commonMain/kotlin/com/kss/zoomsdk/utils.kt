package com.kss.zoomsdk

fun assert(condition: Boolean, message: () -> String) {
    if (!condition) {
        throw AssertionError(message())
    }
}
