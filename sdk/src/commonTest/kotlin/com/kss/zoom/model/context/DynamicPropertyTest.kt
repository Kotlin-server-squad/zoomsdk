package com.kss.zoom.model.context

import com.kss.zoom.model.context.DynamicProperty.Companion.fromDefaultSupplier
import com.kss.zoom.model.context.DynamicProperty.Companion.nullable
import com.kss.zoom.model.context.DynamicProperty.Companion.required
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DynamicPropertyTest {

    @Test
    fun `should create dynamic property with default value`() {
        val property = DynamicProperty("name", "default")
        assertEquals("name", property.name, "Name should be the same")
        assertEquals("default", property.default(), "Default value should be the same")
        assertEquals(String::class, property.type, "Type should be the same")
    }

    @Test
    fun `should create dynamic property with nullable value and default via fromDefaultSupplier`() {
        val property = fromDefaultSupplier("name") { "default" }
        assertEquals("name", property.name, "Name should be the same")
        assertEquals("default", property.default(), "Default value should be the same")
        assertEquals(String::class, property.type, "Type should be the same")
    }

    @Test
    fun `should create dynamic property with required value`() {
        val property = required<String>("name")
        assertEquals("name", property.name, "Name should be the same")
        assertEquals(String::class, property.type, "Type should be the same")
        assertFailsWith(
            exceptionClass = IllegalStateException::class,
            message = "Property name is required",
            block = { property.default() },
        )
    }

    @Test
    fun `should create dynamic property with required value and default`() {
        val property = required("name", "default")
        assertEquals("name", property.name, "Name should be the same")
        assertEquals("default", property.default(), "Default value should be the same")
        assertEquals(String::class, property.type, "Type should be the same")
    }

    @Test
    fun `should create dynamic property with nullable value`() {
        val property = nullable<String>("name")
        assertEquals("name", property.name, "Name should be the same")
        assertEquals(null, property.default(), "Default value should be the same")
        assertEquals(String::class, property.type, "Type should be the same")
    }

    @Test
    fun `should create dynamic property with custom serializer`() {
        val property = required<CustomData>("data").withSerializer(CustomData.serializer())
        assertEquals("data", property.name, "Data should be the same")
        assertEquals(CustomData::class, property.type, "Type should be the same")
        assertFailsWith(
            exceptionClass = IllegalStateException::class,
            message = "Property name is required",
            block = { property.default() },
        )
    }
}

@Serializable
data class CustomData(val id: Long, val value: String)
