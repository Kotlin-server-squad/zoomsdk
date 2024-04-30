package com.kss.zoom.sdk.service.model

import kotlinx.datetime.Instant

data class CacheEntry(
    val key: CacheEntryKey,
    val startedAt: Instant,
    val endedAt: Instant,
    var counter: Int = 0,
    val limit: Int = 6000,
)
