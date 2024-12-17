package com.kss.zoom.sdk.users.model.domain

import kotlinx.serialization.SerialName

class User(
    val firstName: String,
    val lastName: String,
    val id: String,
    val email: String,
    val type: Type,
)