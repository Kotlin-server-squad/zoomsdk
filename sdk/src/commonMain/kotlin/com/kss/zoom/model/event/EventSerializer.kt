package com.kss.zoom.model.event

import com.kss.zoom.model.api.event.toModel
import com.kss.zoom.model.context.DynamicProperty
import com.kss.zoom.model.context.context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import com.kss.zoom.model.api.event.Event as ApiEvent

class EventSerializer(
    vararg properties: DynamicProperty<*>,
) : JsonTransformingSerializer<ApiEvent>(ApiEvent.serializer()) {

    private val context = context(*properties)

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val keys = setOf("event", "event_ts", "account_id")

    override fun transformDeserialize(element: JsonElement): JsonElement {
        // TODO - see MeetingSerializer for example
        return element
    }

    fun toModel(json: String): Event {
        return jsonSerializer.decodeFromString(this, json).toModel(context)
    }
}
