package com.kss.zoom.common.event

import com.kss.zoom.model.context.DynamicProperty
import com.kss.zoom.model.event.Event
import com.kss.zoom.model.api.event.Event as ApiEvent

interface EventHandler {

    // This is where you parse the event to your custom business object of type T
    // and then call the block with the parsed object to do whatever you need to do with it.
    fun on(block: suspend (Event) -> Unit)
    suspend fun on(event: ApiEvent)

    // This is where you add a new property to the context of the event.
    fun <P> add(block: () -> DynamicProperty<P>): DynamicProperty<P>
}

