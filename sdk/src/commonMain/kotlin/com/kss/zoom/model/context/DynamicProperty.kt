package com.kss.zoom.model.context

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


