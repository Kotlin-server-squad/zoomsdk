package com.kss.zoom.model.api.event

import com.kss.zoom.model.serialization.JsonAsMapSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payload(
    @SerialName("account_id") val accountId: String,
    @Serializable(with = JsonAsMapSerializer::class) val data: Map<String, String>,
)
