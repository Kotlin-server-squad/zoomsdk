package com.kss.zoom.sdk

import com.kss.zoom.sdk.model.ZoomModule
import com.kss.zoom.utils.withMDCContext

suspend fun <M : ZoomModule, T> M.withCorrelationId(id: String, block: suspend M.() -> T): T {
    return withMDCContext(mapOf("correlationId" to id)) {
        this.block()
    }
}