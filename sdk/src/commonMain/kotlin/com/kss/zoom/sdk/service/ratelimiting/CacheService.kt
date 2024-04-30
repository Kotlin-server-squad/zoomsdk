package com.kss.zoom.sdk.service.ratelimiting

import com.kss.zoom.sdk.service.model.CacheEntry
import com.kss.zoom.sdk.service.model.CacheEntryKey
import io.ktor.util.collections.*

interface CacheService {
    suspend fun getCache(): ConcurrentMap<CacheEntryKey, CacheEntry>
    suspend fun get(key: CacheEntryKey): CacheEntry?
    suspend fun contains(key: CacheEntryKey): Boolean
    suspend fun insertOrUpdate(key: CacheEntryKey): CacheEntry
    suspend fun checkIfLimitIsReached(key: CacheEntryKey): Boolean
}