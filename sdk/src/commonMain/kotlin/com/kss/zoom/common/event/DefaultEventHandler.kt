package com.kss.zoom.common.event

import com.kss.zoom.model.api.event.toModel
import com.kss.zoom.model.context.DynamicProperty
import com.kss.zoom.model.event.Event
import com.kss.zoom.model.api.event.Event as ApiEvent

class DefaultEventHandler : EventHandler {
    companion object {
        fun handler(init: EventHandler.() -> Unit): EventHandler {
            return DefaultEventHandler().apply(init)
        }
    }

    private lateinit var handler: suspend (Event) -> Unit

    private val properties: MutableList<DynamicProperty<*>> = mutableListOf()

    override fun on(block: suspend (Event) -> Unit) {
        handler = block
    }

    override suspend fun on(event: ApiEvent) {
        val modelEvent = event.toModel(*properties.toTypedArray())
        handler(modelEvent)
    }

    override fun <T> add(block: () -> DynamicProperty<T>): DynamicProperty<T> {
        val property = block()
        properties.add(property)
        return property
    }
}

