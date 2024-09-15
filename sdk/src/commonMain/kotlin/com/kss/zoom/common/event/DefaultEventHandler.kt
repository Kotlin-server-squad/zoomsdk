package com.kss.zoom.common.event

import com.kss.zoom.model.event.Event

class DefaultEventHandler : BaseEventHandler(), DynamicEventHandler {
    companion object {
        fun handler(init: DynamicEventHandler.() -> Unit): DynamicEventHandler {
            return DefaultEventHandler().apply(init)
        }
    }

    private lateinit var handler: suspend (Event) -> Unit

    override fun on(block: suspend (Event) -> Unit) {
        handler = block
    }

    override suspend fun on(event: Event) {
        handler(event)
    }
}

