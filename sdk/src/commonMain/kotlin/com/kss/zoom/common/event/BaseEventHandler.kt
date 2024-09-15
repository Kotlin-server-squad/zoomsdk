package com.kss.zoom.common.event

import com.kss.zoom.model.api.event.toModel
import com.kss.zoom.model.context.DynamicProperty
import com.kss.zoom.model.api.event.Event as ApiEvent

abstract class BaseEventHandler : EventHandler {
    private val properties: MutableList<DynamicProperty<*>> = mutableListOf()

    override fun <T> add(block: () -> DynamicProperty<T>): DynamicProperty<T> {
        val property = block()
        properties.add(property)
        return property
    }

    override suspend fun on(event: ApiEvent) {
        val modelEvent = event.toModel(*properties.toTypedArray())
        on(modelEvent)
    }
}
