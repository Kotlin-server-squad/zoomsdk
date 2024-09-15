package com.kss.zoom.common.event

import com.kss.zoom.model.context.DynamicProperty
import com.kss.zoom.model.event.Event
import com.kss.zoom.model.api.event.Event as ApiEvent

interface EventHandler {
    // Handle the fully initialized event.
    suspend fun on(event: Event)

    // This is where you receive the raw event as per the Zoom API.
    suspend fun on(event: ApiEvent)

    // This is where you add a new property to the event context.
    fun <T> add(block: () -> DynamicProperty<T>): DynamicProperty<T>
}
