package com.kss.zoom.sdk.users.model.api

import com.kss.zoom.sdk.users.model.domain.Type
import com.kss.zoom.sdk.users.model.domain.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserResponse(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val id: String,
    val email: String,
    val type: Int,
)

fun CreateUserResponse.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
        type = Type.fromInt(this.type),
    )
}
