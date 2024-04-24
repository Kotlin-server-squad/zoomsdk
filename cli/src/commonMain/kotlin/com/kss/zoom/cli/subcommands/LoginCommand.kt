package com.kss.zoom.cli.subcommands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import com.kss.zoom.sdk.common.model.Url

class LoginCommand(private val zoom: Zoom) : CliktCommand(help = "Login to Zoom", name = "login") {

    private val terminal = Terminal()
    override fun run() {
        val authUrl = zoom.auth().getAuthorizationUrl(Url("http://localhost:8080/callback"))
        terminal.println("Please sign in to Zoom by visiting ${authUrl.value}")
    }
}