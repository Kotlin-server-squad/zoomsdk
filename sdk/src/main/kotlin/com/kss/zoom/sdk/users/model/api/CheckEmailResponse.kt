package com.kss.zoom.sdk.users.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckEmailResponse(
    @SerialName("existed_email")
    val existedEmail: Boolean? = null,
)
