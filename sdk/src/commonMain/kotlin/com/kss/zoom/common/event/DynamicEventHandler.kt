package com.kss.zoom.common.event

import com.kss.zoom.model.event.Event

interface DynamicEventHandler : EventHandler {

    // This is where you define the block that will be called when the fully initialized event is received.
    fun on(block: suspend (Event) -> Unit)
}

