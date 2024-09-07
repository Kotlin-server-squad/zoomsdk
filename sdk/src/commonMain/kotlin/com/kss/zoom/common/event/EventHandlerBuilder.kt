package com.kss.zoom.common.event

import com.kss.zoom.model.context.DynamicProperty

class EventHandlerBuilder {
    private val properties = mutableListOf<DynamicProperty<*>>()

    fun <T> add(property: DynamicProperty<T>): EventHandlerBuilder {
        properties.add(property)
        return this
    }

    fun build(): EventHandler {
        return DefaultEventHandler()
    }
}
