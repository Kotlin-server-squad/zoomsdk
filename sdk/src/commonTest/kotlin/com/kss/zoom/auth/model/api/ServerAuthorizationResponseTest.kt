package com.kss.zoom.auth.model.api

import com.kss.zoom.auth.model.AccessToken
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerAuthorizationResponseTest {

    @Test
    fun shouldConvertToAccessToken() {
        assertEquals(
            AccessToken("access_token", 1000L),
            ServerAuthorizationResponse(
                accessToken = "access_token",
                tokenType = "token_type",
                expiresIn = 1000L
            ).toAccessToken(),
            "Should convert to AccessToken"
        )
    }
}