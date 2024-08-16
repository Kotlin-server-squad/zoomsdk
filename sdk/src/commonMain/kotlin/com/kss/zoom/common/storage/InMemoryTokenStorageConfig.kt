package com.kss.zoom.common.storage

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class InMemoryTokenStorageConfig(
    val accessTokenExpiry: Duration
) {
    companion object {
        val DEFAULT = InMemoryTokenStorageConfig(accessTokenExpiry = 60.minutes)
    }
}
