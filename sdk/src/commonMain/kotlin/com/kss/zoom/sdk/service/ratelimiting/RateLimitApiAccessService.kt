package com.kss.zoom.sdk.service.ratelimiting

interface RateLimitApiAccessService {
    suspend fun queueTask(task: suspend () -> Any?)
    suspend fun collectResponses(action: suspend (response: Any?) -> Unit)
}