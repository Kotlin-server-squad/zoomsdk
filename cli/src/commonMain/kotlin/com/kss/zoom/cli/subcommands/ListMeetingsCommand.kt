package com.kss.zoom.cli.subcommands

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import com.kss.zoom.sdk.common.call
import kotlinx.coroutines.runBlocking

class ListMeetingsCommand(private val zoom: Zoom) : AuthCommand(help = "List meetings", name = "list-meetings") {
    companion object {
        // TODO replace with a call to the Users SDK to find out your user ID
        const val USER_ID = "lqkrEKqMR1CCmALIVs73RQ"
    }

    private val terminal = Terminal()
    override fun run() {
        if (this.tokens == null) {
            terminal.println(TextColors.red("Please run the login command first."))
            return
        }
        val meetings = zoom.meetings(this.tokens!!)
        val scheduledMeetings = runBlocking {
            call { meetings.listScheduled(USER_ID) }
        }
        terminal.println(TextColors.green("Found ${scheduledMeetings.items.size} meetings"))
        scheduledMeetings.items.forEach {
            terminal.println(it)
        }
    }
}