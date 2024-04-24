package com.kss.zoom.cli.subcommands

import com.github.ajalt.clikt.core.CliktCommand
import com.kss.zoom.auth.model.UserTokens

abstract class AuthCommand(help: String, name: String) : CliktCommand(help = help, name = name) {
    protected var tokens: UserTokens? = null
    fun setUserTokens(tokens: UserTokens) {
        this.tokens = tokens
    }
}