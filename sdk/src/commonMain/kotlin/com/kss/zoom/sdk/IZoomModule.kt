package com.kss.zoom.sdk

import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.webhooks.IWebhookVerifier
import com.kss.zoom.sdk.webhooks.model.Request
import com.kss.zoom.sdk.webhooks.model.api.ZoomEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

interface IZoomModule

abstract class ZoomModuleBase(
    userTokens: UserTokens? = null,
    val webClient: WebClient,
    val webhookVerifier: IWebhookVerifier? = null
) : IZoomModule {
    protected val logger = KotlinLogging.logger {}
    protected var userTokens: UserTokens? = userTokens
        get() = field ?: throw IllegalStateException("User tokens have not been set.")

    protected val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
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
                logger.warn { "No webhook verifier set. Skipping event handling." }
                return Result.success(Unit)
            }
            val result = webhookVerifier.verify(payload, timestamp, signature)
            when {
                result.isFailure -> {
                    logger.warn { "Failed to verify request signature: ${result.exceptionOrNull()?.message}" }
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
            logger.warn(e) { "Failed to parse event $eventName" }
            return Result.failure(e)
        }
    }

    protected suspend inline fun <reified E : Any> handleEvent(
        request: Request,
        eventName: String,
        action: (E) -> Unit
    ): Result<Unit> {
        try {
            if (webhookVerifier == null) {
                logger.warn { "No webhook verifier set. Skipping event handling." }
                return Result.success(Unit)
            }
            val result = webhookVerifier.verify(request)
            return when {
                result.isFailure -> {
                    logger.warn { "Failed to verify request signature: ${result.exceptionOrNull()?.message}" }
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
            logger.warn(e) { "Failed to parse event $eventName" }
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
            logger.debug { "Ignoring event: ${zoomEvent.event}" }
            return Result.success(Unit)
        }
        val targetEvent = json.decodeFromString<E>(payload)
        logger.debug { "Received $eventName event: $targetEvent" }
        action(targetEvent)
        return Result.success(Unit)
    }
}