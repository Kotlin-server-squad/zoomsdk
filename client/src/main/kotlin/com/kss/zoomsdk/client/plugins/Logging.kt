package com.kss.zoomsdk.client.plugins

import io.ktor.client.*
import io.ktor.client.plugins.logging.*

fun HttpClientConfig<*>.configureLogging() {
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }
}