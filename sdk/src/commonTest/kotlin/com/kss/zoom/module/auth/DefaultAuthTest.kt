package com.kss.zoom.module.auth

import com.kss.zoom.client.ApiClient
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.Auth.Companion.FORM_URL_ENCODED_CONTENT_TYPE
import com.kss.zoom.module.auth.model.AuthConfig
import com.kss.zoom.module.auth.model.AuthResponse
import dev.mokkery.answering.calls
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DefaultAuthTest {

    companion object {
        private val config = AuthConfig(
            clientId = "clientId",
            clientSecret = "clientSecret"
        )
    }

    @Test
    fun `should provide correctly encoded authorization url`() = runTest {
        withMockClient { auth ->
            assertEquals(
                "${config.baseUrl}/oauth/authorize?response_type=code&client_id=clientId&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fcallback",
                auth.getAuthorizationUrl("http://localhost:8080/callback"),
                "Authorization URL should be equal"
            )
        }
    }

    @Test
    fun `should exchange authorization code for user tokens`() = runTest {
        withMockClient(
            mockClient = mock {
                everySuspend {
                    post<AuthResponse>(
                        url = "${config.baseUrl}/oauth/token",
                        clientId = config.clientId,
                        clientSecret = config.clientSecret,
                        contentType = FORM_URL_ENCODED_CONTENT_TYPE,
                        body = null
                    )
                } calls { _ ->
                    CallResult.Success(
                        AuthResponse(
                            accessToken = "accessToken",
                            refreshToken = "refreshToken",
                            tokenType = "tokenType",
                            expiresIn = 3600
                        )
                    )
                }
            }
        ) { auth ->
            when (val result = auth.authorize("code")) {
                is CallResult.Success -> {
                    val userTokens = result.data
                    assertEquals("accessToken", userTokens.accessToken, "accessToken should be equal")
                    assertEquals("refreshToken", userTokens.refreshToken, "refreshToken should be equal")
                    assertEquals("tokenType", userTokens.tokenType, "tokenType should be equal")
                    assertEquals(3600, userTokens.expiresIn, "expiresIn should be equal")
                }

                else -> fail("Unexpected result: $result")
            }
        }
    }

    @Test
    fun `should refresh user authorization`() = runTest {
        withMockClient(
            mockClient = mock {
                everySuspend {
                    post<AuthResponse>(
                        url = "${config.baseUrl}/oauth/token",
                        body = "grant_type=refresh_token&refresh_token=refreshToken",
                        contentType = FORM_URL_ENCODED_CONTENT_TYPE,
                    )
                } calls { _ ->
                    CallResult.Success(
                        AuthResponse(
                            accessToken = "newAccessToken",
                            refreshToken = "newRefreshToken",
                            tokenType = "tokenType",
                            expiresIn = 3600
                        )
                    )
                }
            }
        ) { auth ->
            when (val result = auth.reauthorize("refreshToken")) {
                is CallResult.Success -> {
                    val userTokens = result.data
                    assertEquals("newAccessToken", userTokens.accessToken, "accessToken should be equal")
                    assertEquals("newRefreshToken", userTokens.refreshToken, "refreshToken should be equal")
                    assertEquals("tokenType", userTokens.tokenType, "tokenType should be equal")
                    assertEquals(3600, userTokens.expiresIn, "expiresIn should be equal")
                }

                else -> fail("Unexpected result: $result")
            }
        }
    }

    private suspend fun withMockClient(mockClient: ApiClient = mock {}, block: suspend (auth: DefaultAuth) -> Unit) {
        block(DefaultAuth(config, mockClient))
    }
}
