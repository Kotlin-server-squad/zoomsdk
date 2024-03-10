package com.kss.zoom.sdk

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.model.api.events.ZoomEvent
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.cancellation.CancellationException

interface ZoomModule

abstract class ZoomModuleBase(
    userTokens: UserTokens,
    val client: WebClient,
    val webhookVerifier: WebhookVerifier? = null
) : ZoomModule {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)
    protected var userTokens: UserTokens? = userTokens
        get() = field ?: throw IllegalStateException("User tokens have not been set.")

    protected suspend inline fun <reified E : Any> handleEvent(
        call: ApplicationCall,
        eventName: String,
        action: (E) -> Unit
    ) {
        try {
            if (webhookVerifier == null) {
                logger.warn("No webhook verifier set. Skipping event handling.")
                return
            }
            if (webhookVerifier.verify(call).not()) {
                logger.warn("Failed to verify request signature")
                return
            }
            val zoomEvent = call.receive<ZoomEvent>()
            if (zoomEvent.event != eventName) {
                logger.debug("Ignoring event: {}", zoomEvent.event)
                return
            }
            val targetEvent = Json.decodeFromJsonElement<E>(zoomEvent.payload)
            logger.debug("Received {} event: {}", eventName, targetEvent)
            action(targetEvent)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.warn("Failed to parse event {}: {}", eventName, e.message)
        }
    }
}