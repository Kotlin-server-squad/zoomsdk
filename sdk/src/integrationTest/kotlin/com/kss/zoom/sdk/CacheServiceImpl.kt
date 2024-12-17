package com.kss.zoom.sdk

import io.ktor.util.collections.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import sun.jvm.hotspot.oops.CellTypeState.value
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class CacheServiceImpl {
    private val map = ConcurrentHashMap<Type,CacheEntry>()
    private val mutexMap = ConcurrentHashMap<Type, Mutex>()

    suspend fun get(key: Type): CacheEntry? {
        mutexMap.getOrPut(key) { Mutex() }.withLock {
            val now = Instant.now()
            val after24hours = now.minusSeconds(24 * 60 * 60)
            return map.getOrPut(key) { CacheEntry(0, now,after24hours) }
        }
    }

    suspend fun put(key: Type, value: CacheEntry) {
        mutexMap.getOrPut(key) { Mutex() }.withLock {
            map[key] = value
        }
    }

    fun evictOldEntries(lifetimeThreshold: Long = 24 * 60 * 60) { // threshold in seconds
        val currentTime = Instant.now()
        map.entries.removeIf { (key, entry) ->
            Duration.between(entry.nextAccess, currentTime).seconds > lifetimeThreshold
        }
    }
}