package com.kss.zoom.utils

import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.withContext
import org.slf4j.MDC
import kotlin.coroutines.CoroutineContext

class MDCContext(private val mdc: Map<String, String>) : ThreadContextElement<Map<String, String>> {
    companion object Key : CoroutineContext.Key<MDCContext>

    override val key: CoroutineContext.Key<*>
        get() = Key

    override fun updateThreadContext(context: CoroutineContext): Map<String, String> {
        // Backup the old MDC context
        val oldMDC = MDC.getCopyOfContextMap() ?: emptyMap()
        // Set the new MDC context data for the coroutine
        MDC.setContextMap(mdc)
        // Return the old state so it can be restored later
        return oldMDC
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: Map<String, String>) {
        // Restore the old MDC context when the coroutine is done or is switching back
        if (oldState.isEmpty()) {
            MDC.clear()
        } else {
            MDC.setContextMap(oldState)
        }
        MDC.setContextMap(oldState)
    }
}

suspend fun <T> withMDCContext(mdcData: Map<String, String>, block: suspend () -> T): T =
    withContext(MDCContext(mdcData)) {
        block()
    }