package com.kss.zoom.sdk

import java.time.Instant

data class CacheEntry(
    var counter: Int = 0,
    var firstAccess: Instant,
    var nextAccess: Instant,
)