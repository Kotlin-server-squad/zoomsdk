package com.kss.zoom.auth.model.api

import com.kss.zoom.auth.model.AccessToken
import com.kss.zoom.auth.model.RefreshToken
import com.kss.zoom.auth.model.UserTokens
import kotlin.test.Test
import kotlin.test.assertEquals

class UserAuthorizationResponseTest {

    @Test
    fun shouldConvertToUserTokens() {
        assertEquals(
            UserTokens(
                accessToken = AccessToken("access_token", 1000L),
                refreshToken = RefreshToken("refresh_token")
            ),
            UserAuthorizationResponse(
                accessToken = "access_token",
                tokenType = "token_type",
                refreshToken = "refresh_token",
                expiresIn = 1000L
            ).toUserTokens(),
            "Should convert to UserTokens"
        )
    }
}