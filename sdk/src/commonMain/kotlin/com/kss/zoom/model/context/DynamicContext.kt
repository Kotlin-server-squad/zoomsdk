package com.kss.zoom.model.context

import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

class DynamicContext(vararg properties: DynamicPropertyValue<*>) {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val properties = HashMap<DynamicProperty<*>, Any?>().apply {
        properties.forEach { put(it.property, it.value) }
    }

    private val unsetProperties = mutableListOf<DynamicProperty<*>>()

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


    override fun toString(): String = properties.map { it.key.name to it.value }.toMap().toString()

    @OptIn(ExperimentalSerializationApi::class)
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
                Instant::class -> Instant.parse(value)
                else -> property.serializer?.let {
                    if (it.descriptor.kind is PrimitiveKind) {
                        jsonSerializer.decodeFromJsonElement(it, JsonPrimitive(value))
                    } else {
                        jsonSerializer.decodeFromString(it, value)
                    }
                } ?: property.cast(value)
            }
            propertyValue?.let { properties[property] = property.cast(it) }
        } catch (e: Exception) {
            throw IllegalArgumentException("Error casting property ${property.name} to type ${property.type.qualifiedName} with value $value")
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DynamicContext

        if (properties != other.properties) return false
        if (unsetProperties != other.unsetProperties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = properties.hashCode()
        result = 31 * result + unsetProperties.hashCode()
        return result
    }
}

fun context(init: DynamicContext.() -> Unit) = DynamicContext().apply(init)

fun context(vararg property: DynamicProperty<*>): DynamicContext {
    val c = DynamicContext()
    property.forEach {
        c.set(it)
    }
    return c
}

