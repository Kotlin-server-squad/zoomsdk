package com.kss.zoom.cli

import kotlinx.cinterop.toKString
import platform.posix.getenv

actual fun getSystemProperty(name: String): String? = getenv(name)?.toKString()