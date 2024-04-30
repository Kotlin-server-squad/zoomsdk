package com.kss.zoom.sdk.service.ratelimiting

import com.kss.zoom.sdk.service.model.CacheEntry
import com.kss.zoom.sdk.service.model.CacheEntryKey

interface CacheManagerService {
    suspend fun insertOrUpdate(key: CacheEntryKey): CacheEntry
    suspend fun get(key: CacheEntryKey): CacheEntry?
}