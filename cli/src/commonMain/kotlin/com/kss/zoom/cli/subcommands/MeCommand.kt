package com.kss.zoom.cli.subcommands

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import com.kss.zoom.sdk.common.call
import com.kss.zoom.sdk.users.model.domain.UserInfo
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class MeCommand(private val zoom: Zoom) : AuthCommand(help = "Get user information", name = "me") {
    private val terminal = Terminal()
    private val json = Json { prettyPrint = true }

    override fun run() {
        if (this.tokens == null) {
            terminal.println(TextColors.red("Please run the login command first."))
            return
        }
        val users = zoom.users(this.tokens!!)
        val me = runBlocking {
            call { users.me() }
        }
        val jsonObject = json.encodeToString(UserInfo.serializer(), me)
        terminal.println(jsonObject)
    }
}