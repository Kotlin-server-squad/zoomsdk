package com.kss.zoom.auth.config

import com.kss.zoom.auth.ClientId
import com.kss.zoom.auth.ClientSecret

data class AuthorizationConfig(
    val clientId: ClientId,
    val clientSecret: ClientSecret,
    val baseUrl: String
) {
    companion object {
        fun create(clientId: String, clientSecret: String, baseUrl: String = "https://zoom.us"): AuthorizationConfig {
            return AuthorizationConfig(ClientId(clientId), ClientSecret(clientSecret), baseUrl)
        }
    }
}