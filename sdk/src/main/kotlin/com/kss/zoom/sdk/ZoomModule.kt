package com.kss.zoom.sdk

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.model.api.events.ZoomEvent
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
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

    protected val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    protected suspend inline fun <reified E : Any> handleEvent(
        call: ApplicationCall,
        eventName: String,
        action: (E) -> Unit
    ): Result<Unit> {
        try {
            if (webhookVerifier == null) {
                logger.warn("No webhook verifier set. Skipping event handling.")
                return Result.success(Unit)
            }
            val result = webhookVerifier.verify(call)
            if (result.isFailure) {
                logger.warn("Failed to verify request signature: {}", result.exceptionOrNull()?.message)
                return Result.failure(
                    result.exceptionOrNull() ?: IllegalStateException("Failed to verify request signature")
                )
            }
            val jsonString = result.getOrThrow()
            val zoomEvent = json.decodeFromString<ZoomEvent>(jsonString)

            if (zoomEvent.event != eventName) {
                logger.debug("Ignoring event: {}", zoomEvent.event)
                return Result.success(Unit)
            }
            val targetEvent = json.decodeFromString<E>(jsonString)
            logger.debug("Received {} event: {}", eventName, targetEvent)
            action(targetEvent)
            return Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.warn("Failed to parse event {}", eventName, e)
            return Result.failure(e)
        }
    }
}