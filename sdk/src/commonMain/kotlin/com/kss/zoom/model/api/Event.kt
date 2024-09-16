package com.kss.zoom.model.api

import com.kss.zoom.model.api.Event.Companion.OBJECT_KEY
import com.kss.zoom.model.context.DynamicProperty
import com.kss.zoom.model.context.context
import com.kss.zoom.model.serialization.JsonAsMapSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.kss.zoom.model.event.Event as EventModel

@Serializable
data class Event(
    @SerialName("event") val name: String,
    @SerialName("event_ts") val timestamp: Long,
    @Serializable(with = JsonAsMapSerializer::class)
    val payload: Map<String, String>,
) {
    companion object {
        const val OBJECT_KEY = "object"
    }
}

fun Event.toModel(json: Json, vararg property: DynamicProperty<*>): EventModel {
    val fieldMap = payload.filterKeys { it != OBJECT_KEY }
    val objectMap = payload[OBJECT_KEY]?.let { json.decodeFromString(JsonAsMapSerializer, it) } ?: emptyMap()
    return EventModel(
        name = name,
        timestamp = timestamp,
        context = context(*property).fromMap(fieldMap + objectMap),
    )
}

