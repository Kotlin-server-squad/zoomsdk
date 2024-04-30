package com.kss.zoom.sdk.service.ratelimiting.impl

import com.kss.zoom.sdk.service.model.CacheEntryKey
import com.kss.zoom.sdk.service.ratelimiting.CacheManagerService
import com.kss.zoom.sdk.service.ratelimiting.RateLimitApiAccessService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.datetime.Clock

class RateLimitApiAccessServiceImpl(
    private val cacheManagerService: CacheManagerService,
    private val maxRequestsPerSecond: Int = 4,
) : RateLimitApiAccessService {
    private val requestQueue = Channel<suspend () -> Any?>()
    private val responseQueue = Channel<Any?>()
    private val semaphore = Semaphore(maxRequestsPerSecond)

    val handler =
        CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
//            logger.error("Caught $exception")
        }

    private val processingScope = CoroutineScope(Dispatchers.Default + SupervisorJob() + handler)

    init {
        startProcessingRequests()
    }

   private fun startProcessingRequests() {
        processingScope.launch {
            while (isActive) {
                for (task in requestQueue) {
                    throttleRequestRate()
                    semaphore.withPermit {
                        println("Executing task")
//                    logger.info("Executing task")
                        processingScope.launch {
                            val result = task()
                            responseQueue.send(result)
                        }
                    }
                }
            }
        }
    }


    override suspend fun queueTask(task: suspend () -> Any?) {
        processingScope.launch {
            requestQueue.send(task)
        }
    }

    override suspend fun collectResponses(action: suspend (response: Any?) -> Unit) {
        for (response in responseQueue) {
            println("Received response: $response")
//            logger.info("Received response: $response")
            action(response)
        }
    }

    private suspend fun throttleRequestRate() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val nextAllowedTime = Clock.System.now().toEpochMilliseconds() + 1000 / maxRequestsPerSecond
        if (currentTime < nextAllowedTime) {
            delay(nextAllowedTime - currentTime)
        }
    }

}