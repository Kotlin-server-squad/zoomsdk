package com.kss.zoom.cli

actual fun getSystemProperty(name: String): String? = System.getProperty(name)