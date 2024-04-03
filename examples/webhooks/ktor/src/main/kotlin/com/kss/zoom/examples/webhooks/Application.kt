package com.kss.zoom.examples.webhooks

import com.kss.zoom.Zoom
import com.kss.zoom.examples.webhooks.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.config.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    fun getConfigString(path: String): String {
        return environment.config.tryGetString(path) ?: error("No $path found")
    }
    val clientId = getConfigString("ktor.zoom.client-id")
    val clientSecret = getConfigString("ktor.zoom.client-secret")
    val verificationToken = getConfigString("ktor.zoom.verification-token")
    val meetings = Zoom.create(
        clientId = clientId,
        clientSecret = clientSecret,
        verificationToken = verificationToken
    ).meetings()
    configureRouting(meetings)
}