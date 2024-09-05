package com.kss.zoom.model

import kotlin.reflect.KClass

interface DynamicProperty<T> {
    val name: String
    val type: KClass<*>
    fun cast(value: Any?): T
    fun default(): T

    companion object {
        inline fun <reified T> fromDefaultSupplier(name: String, crossinline default: () -> T) =
            object : DynamicProperty<T> {
                override val name = name
                override fun cast(value: Any?): T = value as T
                override fun default(): T = default()
                override val type = T::class
            }

        inline operator fun <reified T> invoke(name: String, default: T) = fromDefaultSupplier(name) { default }

        inline fun <reified T> required(name: String) = fromDefaultSupplier<T>(name) {
            throw IllegalStateException("Property is required")
        }

        inline fun <reified T> required(name: String, default: T) = fromDefaultSupplier(name) { default }

        inline fun <reified T> nullable(name: String) = DynamicProperty<T?>(name, null)

    }

    operator fun <T> DynamicProperty<T>.invoke(value: T) = DynamicPropertyValue(this, value)
}

class DynamicContext(vararg properties: DynamicPropertyValue<*>) {

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
                        else -> null
                    }
                    propertyValue?.let { properties[entry.key] = it }
                } catch (e: ClassCastException) {
                    // Ignore
                }
            }
        }

//        map.map { (key, value) ->
//            val property = properties.filterKeys { property -> property.name == key }.firstNotNullOf { it.key }
//            when (property.type) {
//                String::class -> set(property, value)
//                Int::class -> set(property, value.toInt())
//                Long::class -> set(property, value.toLong())
//                else -> throw IllegalArgumentException("Unsupported type ${property.type}")
//            }
//        }
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

data class DynamicPropertyValue<T>(val property: DynamicProperty<T>, val value: T)

