package com.kss.zoom.sdk.model.api.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ZoomEvent(
    val event: String, // This is used to identify the event type
    val payload: JsonElement // Keep as JsonElement for now; we'll parse it later
)
