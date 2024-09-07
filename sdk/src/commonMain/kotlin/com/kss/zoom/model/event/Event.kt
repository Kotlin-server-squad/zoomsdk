package com.kss.zoom.model.event

import com.kss.zoom.model.context.DynamicContext

data class Event(
    val name: String,
    val timestamp: Long,
    val context: DynamicContext
)
