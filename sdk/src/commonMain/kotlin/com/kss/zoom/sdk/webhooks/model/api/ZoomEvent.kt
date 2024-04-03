package com.kss.zoom.sdk.webhooks.model.api

import kotlinx.serialization.Serializable

@Serializable
data class ZoomEvent(val event: String)
