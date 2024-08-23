package com.kss.zoom.common

actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun getProperty(name: String): String? {
    return System.getProperty(name)
}
