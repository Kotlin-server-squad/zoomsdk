package com.kss.zoom.sdk.common

actual suspend fun <T> withMDCContext(mdcData: Map<String, String>, block: suspend () -> T): T {
    return block()
}