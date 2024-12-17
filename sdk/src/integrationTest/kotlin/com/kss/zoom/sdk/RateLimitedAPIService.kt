package com.kss.zoom.sdk

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import kotlin.random.Random
import kotlin.random.nextInt

class RateLimitedAPIService(
    private val maxRequestsPerSecond: Int = 4
) {
    private val requestQueue = Channel<suspend () -> Any?>(Channel.UNLIMITED)
    private val responseChannel = Channel<Any?>(Channel.UNLIMITED)
    private val semaphore = Semaphore(maxRequestsPerSecond)

    val handler =
        CoroutineExceptionHandler { ctx, exception ->
            println("Caught $exception")
        }

    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + handler)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            for (task in requestQueue) {
                throttleRequestRate()
                semaphore.withPermit {
                    logger.info("Executing task")
                    scope.launch {
                        val result = task()
                        responseChannel.send(result)
                    }
                }
            }
        }
    }

    private suspend fun throttleRequestRate() {
        val currentTime = System.currentTimeMillis()
        val nextAllowedTime = System.currentTimeMillis() + 1000 / maxRequestsPerSecond
        if (currentTime < nextAllowedTime) {
            delay(nextAllowedTime - currentTime)
        }
    }

    suspend fun queueTask(task: suspend () -> Any?): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            requestQueue.send(task)
        }
    }

    suspend fun collectResponses(action: suspend (response: Any?) -> Unit) {
        for (response in responseChannel) {
            logger.info("Received response: $response")
            action(response)
        }
    }

    suspend fun simulateApiCall(taskId: Int): String {
        logger.info("Starting API Call $taskId")
        delay(5000) // Simulate network delay
        val response = "Response from API Call $taskId"
        logger.info(response)
        if (Random.nextInt(1..10) > 5) {
            throw RuntimeException("API Call $taskId failed")
        }
        return response

    }

    companion object {
        private val logger = LoggerFactory.getLogger(RateLimitedAPIService::class.java)
    }
}

