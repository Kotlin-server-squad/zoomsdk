package com.kss.zoom.sdk.service.ratelimiting.impl

import com.kss.zoom.sdk.service.model.CacheEntry
import com.kss.zoom.sdk.service.model.CacheEntryKey
import com.kss.zoom.sdk.service.ratelimiting.CacheManagerService
import com.kss.zoom.sdk.service.ratelimiting.CacheService
import io.ktor.util.collections.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

class CacheManagerServiceImpl(val cacheService: CacheService) : CacheManagerService {
    private val cacheMutex = ConcurrentMap<CacheEntryKey,Mutex>()
    val scope = CoroutineScope(Dispatchers.Default)

    init {
        startEvictionProcess()
    }

    override suspend fun insertOrUpdate(key: CacheEntryKey): CacheEntry {
        cacheMutex.getOrPut(key) { Mutex() }.withLock {
            return cacheService.insertOrUpdate(key)
        }
    }
    override suspend fun get(key: CacheEntryKey): CacheEntry? {
        cacheMutex.getOrPut(key) { Mutex() }.withLock {
            if(cacheService.contains(key)) {
                return cacheService.get(key)
            }else{
                return null
            }
        }
    }



    private fun startEvictionProcess() {
        scope.launch {
            while (isActive) {
                val now = Clock.System.now()
                val cache = cacheService.getCache()
                val keysToRemove = cache.entries.filter {
                    now < it.value.endedAt
                }.map { it.key }

                keysToRemove.forEach {
                    cacheMutex.getOrPut(it) { Mutex() }.withLock {
                        cache.remove(it)
                    }
                }
                delay(60000) // Check every minute
            }
        }
    }


    private fun endEvictionProcess() {
        scope.cancel()
    }
}