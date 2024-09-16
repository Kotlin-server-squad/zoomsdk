package com.kss.zoom.common.event

import com.kss.zoom.model.api.toModel
import com.kss.zoom.model.context.DynamicProperty
import kotlinx.serialization.json.Json
import com.kss.zoom.model.api.Event as ApiEvent

abstract class BaseEventHandler : EventHandler {
    private val properties: MutableList<DynamicProperty<*>> = mutableListOf()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override fun <T> add(block: () -> DynamicProperty<T>): DynamicProperty<T> {
        val property = block()
        properties.add(property)
        return property
    }

    override suspend fun on(event: ApiEvent) {
        val modelEvent = event.toModel(json, *properties.toTypedArray())
        on(modelEvent)
    }
}
