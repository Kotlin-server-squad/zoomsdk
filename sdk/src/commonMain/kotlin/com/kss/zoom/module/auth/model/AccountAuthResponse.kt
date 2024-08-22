package com.kss.zoom.module.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountAuthResponse(
    @SerialName("access_token") val accessToken: String,
)

fun AccountAuthResponse.toAccountToken(): AccountToken =
    AccountToken(accessToken = accessToken)
