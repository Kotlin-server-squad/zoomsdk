package com.kss.zoom.sdk.common

actual suspend fun <T> withMDCContext(mdcData: Map<String, String>, block: suspend () -> T): T {
    // TODO MDCContext is not available in JS
    return block()
}