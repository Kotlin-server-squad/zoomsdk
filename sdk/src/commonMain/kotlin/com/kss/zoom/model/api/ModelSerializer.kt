package com.kss.zoom.model.api

import com.kss.zoom.model.DynamicContext
import com.kss.zoom.model.DynamicProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

open class ModelSerializer<T : Model<*>>(
    serializer: KSerializer<T>,
    private val keys: Set<String>,
    private val context: DynamicContext,
) : JsonTransformingSerializer<T>(serializer) {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        val jsonObject = if (element is JsonObject) element else {
            return element
        }
        val knownKeys = keys + "id"
        val knownFields = JsonObject(jsonObject.filterKeys { it in knownKeys })
        val unknownFields = JsonObject(jsonObject.filterKeys { it !in knownKeys })

        val result = buildJsonObject {
            knownFields.forEach { (key, value) -> put(key, value) }
            put("data", buildJsonObject {
                unknownFields.forEach { (key, value) ->
                    context[key]?.let { property ->
                        jsonElement(property, value)?.let {
                            put(key, it)
                        }
                    }
                }
            })
        }
        return result
    }

    fun toModel(json: String): T {
        return jsonSerializer.decodeFromString(this, json)
    }

    private fun jsonElement(property: DynamicProperty<*>, value: JsonElement): JsonElement? {
        return try {
            when (value) {
                is JsonPrimitive -> jsonPrimitiveElement(property, value)
                is JsonObject -> jsonObjectElement(property, value)
                is JsonArray -> jsonArrayElement(property, value)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun jsonPrimitiveElement(property: DynamicProperty<*>, value: JsonPrimitive): JsonPrimitive {
        return when (property.type) {
            String::class -> JsonPrimitive(value.jsonPrimitive.contentOrNull)
            Int::class -> JsonPrimitive(value.jsonPrimitive.intOrNull)
            Long::class -> JsonPrimitive(value.jsonPrimitive.longOrNull)
            Short::class -> JsonPrimitive(value.jsonPrimitive.intOrNull?.toShort())
            Float::class -> JsonPrimitive(value.jsonPrimitive.floatOrNull)
            Double::class -> JsonPrimitive(value.jsonPrimitive.doubleOrNull)
            Boolean::class -> JsonPrimitive(value.jsonPrimitive.booleanOrNull)
            else -> value
        }
    }

    private fun jsonObjectElement(property: DynamicProperty<*>, value: JsonObject): JsonObject {
        return buildJsonObject {
            value.forEach { (key, value) ->
                jsonElement(property, value)?.let {
                    put(key, it)
                }
            }
        }
    }

    private fun jsonArrayElement(property: DynamicProperty<*>, value: JsonArray): JsonArray {
        return buildJsonArray {
            value.forEach { element ->
                jsonElement(property, element)?.let {
                    add(it)
                }
            }
        }
    }
}
