package com.kss.zoom.examples.webhooks.plugins

import com.kss.zoom.sdk.meetings.IMeetings
import com.kss.zoom.sdk.meetings.onMeetingCreated
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

fun Application.configureRouting(meetings: IMeetings) {
    val logger = LoggerFactory.getLogger("Routing")

    routing {
        route("/webhooks") {
            post("/meetings") {
                launch(Dispatchers.IO) {
                    meetings.onMeetingCreated(call) { event ->
                        notifyClients(event, logger, json)
                    }
                }
                logger.info("Responding OK to meetings webhook")
                call.respond("OK")
            }
        }
    }
}

val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

inline fun<reified T> notifyClients(event: T, logger: Logger, json: Json) {
    val message = json.encodeToString(event)
    logger.info(message)
}