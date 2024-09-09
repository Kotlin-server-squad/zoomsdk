package com.kss.zoom.model.context

import kotlinx.serialization.json.Json

class DynamicContext(vararg properties: DynamicPropertyValue<*>) {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val properties = HashMap<DynamicProperty<*>, Any?>().apply {
        properties.forEach { put(it.property, it.value) }
    }

    private val unsetProperties = ArrayDeque<DynamicProperty<*>>()

    fun fromMap(map: Map<String, String>): DynamicContext {
        map.forEach { (key, value) ->
            // First check if the property is already set and replace it
            properties.keys.firstOrNull { it.name == key }?.let { property ->
                cast(property, value)
            } ?: run {
                // If the property is not set, check if it is in the unset properties
                unsetProperties.firstOrNull { it.name == key }?.let { property ->
                    cast(property, value)
                    unsetProperties.remove(property)
                }
            }
        }

        return this
    }

    private fun cast(property: DynamicProperty<*>, value: String) {
        try {
            val propertyValue = when (property.type) {
                String::class -> value
                Byte::class -> value.toByte()
                Short::class -> value.toShort()
                Int::class -> value.toInt()
                Long::class -> value.toLong()
                Float::class -> value.toFloat()
                Double::class -> value.toDouble()
                Boolean::class -> value.toBoolean()
                else -> property.serializer?.let { jsonSerializer.decodeFromString(it, value) }
            }
            propertyValue?.let { properties[property] = property.cast(it) }
        } catch (e: Exception) {
            // Ignore invalid values
        }
    }

    operator fun get(name: String): DynamicProperty<*>? = properties.keys.firstOrNull { it.name == name }

    operator fun <T> get(property: DynamicProperty<T>): T =
        if (properties.contains(property)) {
            property.cast(properties[property])
        } else {
            property.default()
        }

    fun <T> set(property: DynamicProperty<T>) {
        try {
            properties[property] = property.default()
        } catch (e: IllegalStateException) {
            // Uninitialized property
            unsetProperties.add(property)
        }
    }

    operator fun <T> set(property: DynamicProperty<T>, value: T) {
        properties[property] = value
    }

    operator fun <T> DynamicProperty<T>.minus(value: T) = set(this, value)
}

fun context(init: DynamicContext.() -> Unit) = DynamicContext().apply(init)

fun context(vararg property: DynamicProperty<*>): DynamicContext {
    val c = DynamicContext()
    property.forEach {
        c.set(it)
    }
    return c
}

