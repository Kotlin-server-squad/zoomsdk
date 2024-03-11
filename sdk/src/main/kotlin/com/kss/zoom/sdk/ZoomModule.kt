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
            return when {
                result.isFailure -> {
                    logger.warn("Failed to verify request signature: {}", result.exceptionOrNull()?.message)
                    Result.failure(
                        result.exceptionOrNull() ?: IllegalStateException("Failed to verify request signature")
                    )
                }

                else -> {
                    handleEvent<E>(result.getOrThrow(), eventName, action)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.warn("Failed to parse event {}", eventName, e)
            return Result.failure(e)
        }
    }

    protected suspend inline fun <reified E : Any> handleEvent(
        eventName: String,
        payload: String,
        timestamp: Long,
        signature: String,
        action: (E) -> Unit
    ): Result<Unit> {
        try {
            if (webhookVerifier == null) {
                logger.warn("No webhook verifier set. Skipping event handling.")
                return Result.success(Unit)
            }
            val result = webhookVerifier.verify(payload, timestamp, signature)
            when {
                result.isFailure -> {
                    logger.warn("Failed to verify request signature: {}", result.exceptionOrNull()?.message)
                    return Result.failure(
                        result.exceptionOrNull() ?: IllegalStateException("Failed to verify request signature")
                    )
                }

                else -> {
                    return handleEvent(result.getOrThrow(), eventName, action)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.warn("Failed to parse event {}", eventName, e)
            return Result.failure(e)
        }
    }

    protected inline fun <reified E : Any> handleEvent(
        payload: String,
        eventName: String,
        action: (E) -> Unit
    ): Result<Unit> {
        val zoomEvent = json.decodeFromString<ZoomEvent>(payload)

        if (zoomEvent.event != eventName) {
            logger.debug("Ignoring event: {}", zoomEvent.event)
            return Result.success(Unit)
        }
        val targetEvent = json.decodeFromString<E>(payload)
        logger.debug("Received {} event: {}", eventName, targetEvent)
        action(targetEvent)
        return Result.success(Unit)
    }
}