package com.kss.zoom.common

import platform.Foundation.timeIntervalSince1970

actual fun currentTimeMillis(): Long {
    return (platform.Foundation.NSDate().timeIntervalSince1970 * 1000).toLong()
}

actual fun getProperty(name: String): String? {
    return platform.Foundation.NSProcessInfo.processInfo.environment[name].toString()
}
