package com.kss.zoom.sdk.users.model.api

import com.kss.zoom.sdk.users.model.domain.UserPermissions
import kotlinx.serialization.Serializable

@Serializable
data class UserPermissionsResponse(val permissions: List<String>)

fun UserPermissionsResponse.toDomain(): UserPermissions {
    return UserPermissions(
        permissions = this.permissions
    )
}
