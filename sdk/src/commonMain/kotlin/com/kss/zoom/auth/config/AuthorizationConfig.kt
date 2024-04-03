package com.kss.zoom.auth.config

import com.kss.zoom.auth.model.ClientId
import com.kss.zoom.auth.model.ClientSecret
import com.kss.zoom.sdk.common.model.Url

data class AuthorizationConfig(
    val clientId: ClientId,
    val clientSecret: ClientSecret,
    val baseUrl: Url
) {
    companion object {
        fun create(clientId: String, clientSecret: String, baseUrl: String = "https://zoom.us"): AuthorizationConfig =
            AuthorizationConfig(
                ClientId(clientId),
                ClientSecret(clientSecret),
                Url(baseUrl)
            )
    }
}
