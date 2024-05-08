package com.kss.zoom.cli.subcommands

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import com.kss.zoom.Zoom
import com.kss.zoom.cli.await
import com.kss.zoom.sdk.meetings.model.ScheduledMeeting
import kotlinx.serialization.json.Json


class ListMeetingsCommand(private val zoom: Zoom) : AuthCommand(help = "List meetings", name = "list-meetings") {

    private val terminal = Terminal()
    private val json = Json { prettyPrint = true }
    override fun run() {
        if (this.tokens == null) {
            terminal.println(TextColors.red("Please run the login command first."))
            return
        }
        val meetings = zoom.meetings(this.tokens!!)
        await(meetings::listScheduled) { scheduledMeetings ->
            terminal.println(TextColors.green("Found ${scheduledMeetings.items.size} meetings"))
            val jsonArray = scheduledMeetings.items.map {
                json.encodeToString(ScheduledMeeting.serializer(), it)
            }
            terminal.println(jsonArray.joinToString(",\n"))
        }
    }
}