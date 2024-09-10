package com.kss.zoom.model.context

import com.kss.zoom.model.context.DynamicProperty.Companion.required
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DynamicContextTest {

    @Test
    fun `should create dynamic context as empty`() {
        val dynamicContext = DynamicContext()
        val name = required<String>("name", "default")
        assertNull(dynamicContext[name.name], "Property should not be present")
        assertEquals("default", dynamicContext[name], "Accessing property should return default value")
    }

    @Test
    fun `should create dynamic context as empty using DSL`() {
        val dynamicContext = context { }
        val name = required<String>("name", "default")
        assertNull(dynamicContext[name.name], "Property should not be present")
        assertEquals("default", dynamicContext[name], "Accessing property should return default value")
    }

    @Test
    fun `should create dynamic context with properties`() {
        val name = required<String>("name")
        val age = required<Int>("age")
        val dynamicContext = DynamicContext(
            DynamicPropertyValue(name, "John"),
            DynamicPropertyValue(age, 30),
        )
        assertEquals("John", dynamicContext[name], "Property should be present")
        assertEquals(30, dynamicContext[age], "Property should be present")
    }

    @Test
    fun `should create dynamic context with properties using DSL`() {
        val name = required<String>("name")
        val age = required<Int>("age")
        val dynamicContext = context {
            name - "John"
            age - 30
        }
        assertEquals("John", dynamicContext[name], "Property should be present")
        assertEquals(30, dynamicContext[age], "Property should be present")
    }

    @Test
    fun `should update dynamic context with new property values`() {
        val name = required<String>("name")
        val age = required<Int>("age")
        val dynamicContext = DynamicContext(
            DynamicPropertyValue(name, "John"),
            DynamicPropertyValue(age, 30),
        )
        dynamicContext.fromMap(mapOf("name" to "Jane", "age" to "25"))
        assertEquals("Jane", dynamicContext[name], "Property should be updated")
        assertEquals(25, dynamicContext[age], "Property should be updated")
    }

    @Test
    fun `should update dynamic context with new property values using DSL`() {
        val name = required<String>("name")
        val age = required<Int>("age")
        val dynamicContext = context(name, age).fromMap(mapOf("name" to "John", "age" to "30"))
        assertEquals("John", dynamicContext[name], "Property should be updated")
        assertEquals(30, dynamicContext[age], "Property should be updated")
    }

    @Test
    fun `should support basic data types`() {
        val string = required<String>("string")
        val byte = required<Byte>("byte")
        val short = required<Short>("short")
        val int = required<Int>("int")
        val long = required<Long>("long")
        val float = required<Float>("float")
        val double = required<Double>("double")
        val boolean = required<Boolean>("boolean")

        val dynamicContext = context(string, byte, short, int, long, float, double, boolean)
            .fromMap(
                mapOf(
                    "string" to "string",
                    "byte" to "1",
                    "short" to "2",
                    "int" to "3",
                    "long" to "4",
                    "float" to "5.0",
                    "double" to "6.0",
                    "boolean" to "true",
                )
            )
        assertEquals("string", dynamicContext[string], "String property should be set")
        assertEquals(1, dynamicContext[byte], "Byte property should be set")
        assertEquals(2, dynamicContext[short], "Short property should be set")
        assertEquals(3, dynamicContext[int], "Int property should be set")
        assertEquals(4, dynamicContext[long], "Long property should be set")
        assertEquals(5.0f, dynamicContext[float], "Float property should be set")
        assertEquals(6.0, dynamicContext[double], "Double property should be set")
        assertEquals(true, dynamicContext[boolean], "Boolean property should be set")
    }

    @Test
    fun `should support custom data types`() {
        val data = required<CustomData>("data").withSerializer(CustomData.serializer())
        val dynamicContext = context(data)
            .fromMap(mapOf("data" to """{"id":1,"value":"value"}"""))
        assertEquals(CustomData(1, "value"), dynamicContext[data], "Custom data property should be set")
    }
}
