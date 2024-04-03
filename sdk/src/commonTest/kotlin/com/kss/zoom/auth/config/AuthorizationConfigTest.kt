package com.kss.zoom.auth.config

import com.kss.zoom.auth.model.ClientId
import com.kss.zoom.auth.model.ClientSecret
import com.kss.zoom.sdk.common.model.Url
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorizationConfigTest {

    @Test
    fun shouldCorrectlyCreateConfig() {
        assertEquals(
            AuthorizationConfig(
                clientId = ClientId("client_id"),
                clientSecret = ClientSecret("client_secret"),
                baseUrl = Url("https://zoom.us")
            ),
            AuthorizationConfig.create("client_id", "client_secret"),
            "Should correctly create config"
        )
    }
}