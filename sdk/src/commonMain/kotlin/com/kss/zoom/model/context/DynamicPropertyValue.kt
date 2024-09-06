package com.kss.zoom.model.context

data class DynamicPropertyValue<T>(val property: DynamicProperty<T>, val value: T)
