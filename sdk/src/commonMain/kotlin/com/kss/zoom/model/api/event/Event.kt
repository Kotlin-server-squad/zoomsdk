package com.kss.zoom.model.api.event

import com.kss.zoom.model.context.DynamicContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.kss.zoom.model.event.Event as EventModel

@Serializable
data class Event(
    @SerialName("event") val name: String,
    @SerialName("event_ts") val timestamp: Long,
    val payload: Payload,
)

fun Event.toModel(context: DynamicContext? = null): EventModel {
    return EventModel(
        name = name,
        timestamp = timestamp,
        context = context?.fromMap(payload.data) ?: DynamicContext(),
    )
}
