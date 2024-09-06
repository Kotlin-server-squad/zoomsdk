package com.kss.zoom.model.context

import kotlinx.serialization.json.Json

class DynamicContext(vararg properties: DynamicPropertyValue<*>) {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun fromMap(map: Map<String, String>): DynamicContext {
        map.forEach { (key, value) ->
            properties.filterKeys { property -> property.name == key }.entries.firstOrNull()?.let { entry ->
                try {
                    val propertyValue = when (entry.key.type) {
                        String::class -> value
                        Byte::class -> value.toByte()
                        Short::class -> value.toShort()
                        Int::class -> value.toInt()
                        Long::class -> value.toLong()
                        Float::class -> value.toFloat()
                        Double::class -> value.toDouble()
                        Boolean::class -> value.toBoolean()
                        Map::class -> jsonSerializer.decodeFromString<Map<String, String>>(value)
                        else -> null
                    }
                    propertyValue?.let { properties[entry.key] = entry.key.cast(it) }
                } catch (e: ClassCastException) {
                    // Ignore
                }
            }
        }

        return this
    }

    private val properties = HashMap<DynamicProperty<*>, Any?>().apply {
        properties.forEach { put(it.property, it.value) }
    }

    operator fun get(name: String): DynamicProperty<*>? = properties.keys.firstOrNull { it.name == name }

    operator fun <T> get(property: DynamicProperty<T>): T =
        if (properties.contains(property)) {
            property.cast(properties[property])
        } else {
            property.default()
        }

    fun <T> set(property: DynamicProperty<T>) {
        properties[property] = property.default()
    }

    operator fun <T> set(property: DynamicProperty<T>, value: T) {
        properties[property] = value
    }

    operator fun <T> DynamicProperty<T>.minus(value: T) = set(this, value)
}

fun context(init: DynamicContext.() -> Unit) = DynamicContext().apply(init)

fun context(vararg property: DynamicProperty<*>): DynamicContext {
    val c = DynamicContext()
    property.forEach { c.set(it) }
    return c
}

