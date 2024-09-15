package com.kss.zoom.examples.webhooks

import kotlinx.coroutines.runBlocking
import org.slf4j.Logger


fun withLogger(loggerName: String, block: suspend (Logger) -> Unit) {
    runBlocking {
        block(org.slf4j.LoggerFactory.getLogger(loggerName))
    }
}
