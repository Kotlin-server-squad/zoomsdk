package com.kss.zoom.sdk.service.ratelimiting.impl

import com.kss.zoom.sdk.service.model.CacheEntry
import com.kss.zoom.sdk.service.model.CacheEntryKey
import com.kss.zoom.sdk.service.ratelimiting.CacheService
import io.ktor.util.collections.*
import kotlinx.datetime.Clock
import kotlin.time.Duration

class CacheServiceImpl : CacheService {
    private val cache = ConcurrentMap<CacheEntryKey, CacheEntry>()
    override suspend fun getCache(): ConcurrentMap<CacheEntryKey, CacheEntry> {
        return cache
    }

    override suspend fun get(key: CacheEntryKey): CacheEntry? {
        if (cache.containsKey(key)) {
            return cache[key]
        } else {
            return null
        }
    }

    override suspend fun contains(key: CacheEntryKey): Boolean {
        return cache.containsKey(key)
    }

    override suspend fun insertOrUpdate(key: CacheEntryKey): CacheEntry {
        if (cache.containsKey(key)) {
            val cacheEntry = cache[key]!!
            cacheEntry.counter++
            return cacheEntry
        } else {
            val startedAt = Clock.System.now()
            val endedAt = startedAt.plus(Duration.parse("PT24H"))
            val cacheEntry = CacheEntry(key, startedAt, endedAt)
            cache[key] = cacheEntry
            return cacheEntry
        }
    }

    override suspend fun checkIfLimitIsReached(key: CacheEntryKey): Boolean {
        return if (cache.containsKey(key)) {
            val cacheEntry = cache[key]!!
            return cacheEntry.counter >= cacheEntry.limit
        } else {
            return false
        }
    }
}