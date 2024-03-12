package com.kss.zoom.demo.webhooks.ktor

import com.kss.zoom.Zoom
import com.kss.zoom.auth.AccessToken
import com.kss.zoom.auth.RefreshToken
import com.kss.zoom.auth.UserTokens
import com.kss.zoom.demo.webhooks.ktor.plugins.configureRouting
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
    ).meetings(
        UserTokens(
            AccessToken("accessToken", 1399L),
            RefreshToken("refreshToken")
        )
    )
    configureRouting(meetings)
}