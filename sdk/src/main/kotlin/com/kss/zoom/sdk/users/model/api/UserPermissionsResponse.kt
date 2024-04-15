package com.kss.zoom.sdk.users.model.api

import kotlinx.serialization.Serializable


@Serializable
data class UserPermissionsResponse(
    val permissions: List<String>
)
