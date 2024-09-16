package com.kss.zoom.model.context

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

interface DynamicProperty<T> {
    val name: String
    val type: KClass<*>
    fun cast(value: Any?): T
    fun default(): T

    var serializer: KSerializer<T>?

    companion object {
        inline fun <reified T> fromDefaultSupplier(name: String, crossinline default: () -> T) =
            object : DynamicProperty<T> {
                override val name = name
                override val type = T::class
                override var serializer: KSerializer<T>? = null
                override fun cast(value: Any?): T = value as T
                override fun default(): T = default()

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other == null || this::class != other::class) return false
                    other as DynamicProperty<*>

                    if (name != other.name) return false
                    if (type != other.type) return false

                    return true
                }

                override fun hashCode(): Int {
                    var result = name.hashCode()
                    result = 31 * result + type.hashCode()
                    return result
                }
            }

        inline operator fun <reified T> invoke(name: String, default: T) = fromDefaultSupplier(name) { default }

        inline fun <reified T> required(name: String) = fromDefaultSupplier<T>(name) {
            throw IllegalStateException("Property $name is required")
        }

        inline fun <reified T> required(name: String, default: T) = fromDefaultSupplier(name) { default }

        inline fun <reified T> nullable(name: String) = fromDefaultSupplier<T?>(name) { null }

    }

    operator fun <T> DynamicProperty<T>.invoke(value: T) = DynamicPropertyValue(this, value)
}

inline fun <reified T> DynamicProperty<T>.withSerializer(serializer: KSerializer<T>): DynamicProperty<T> = apply {
    this.serializer = serializer
}
