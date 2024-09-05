package com.kss.zoom.model

class DynamicProperties(private val data: MutableMap<String, Any?> = mutableMapOf()) {

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(key: String): T? {
        return data[key] as T?
    }

    fun <T> setValue(key: String, value: T) {
        data[key] = value
    }
}
