package com.kss.zoom.model

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*

object JsonAsMapSerializer : JsonTransformingSerializer<Map<String, String>>(
    tSerializer = MapSerializer(
        keySerializer = String.serializer(),
        valueSerializer = String.serializer()
    )
) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return JsonObject(
            element.jsonObject.mapValues { (_, value) ->
                when(value) {
                    is JsonPrimitive -> JsonPrimitive(value.jsonPrimitive.contentOrNull ?: "")
                    is JsonObject -> JsonPrimitive(value.toString())
                    is JsonArray -> JsonPrimitive(value.toString())
                }
//                JsonPrimitive(value.jsonPrimitive.contentOrNull ?: "")
            }
        )
    }
}
