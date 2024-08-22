package com.kss.zoom.module.auth

import com.kss.zoom.client.ApiClient
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.model.AuthConfig
import com.kss.zoom.test.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DefaultAuthTest {

    companion object {
        private val config = AuthConfig(
            clientId = "clientId",
            clientSecret = "clientSecret",
            accountId = "accountId",
        )
    }

    @Test
    fun `should provide correctly encoded authorization url`() = runTest {
        withMockClient {
            assertEquals(
                "${config.baseUrl}/oauth/authorize?response_type=code&client_id=clientId&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fcallback",
                auth(it).getAuthorizationUrl("http://localhost:8080/callback"),
                "Authorization URL should be equal"
            )
        }
    }

    @Test
    fun `should exchange authorization code for user tokens`() = runTest {
        withMockClient(MockEngine { request ->
            request.assertMethod(HttpMethod.Post)
            request.assertUrl("${config.baseUrl}/oauth/token")
            request.assertBasicAuth(config.clientId, config.clientSecret)
            request.assertContentType(ContentType.Application.FormUrlEncoded)
            request.assertBody()

            respond(
                content = ByteReadChannel(
                    """
                    {
                        "access_token": "accessToken",
                        "refresh_token": "refreshToken",
                        "token_type": "tokenType",
                        "expires_in": 3600
                    }
                """.trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }) {
            when (val result = auth(it).authorize("code")) {
                is CallResult.Success -> {
                    val userTokens = result.data
                    assertEquals("accessToken", userTokens.accessToken, "Access token should be equal")
                    assertEquals("refreshToken", userTokens.refreshToken, "Refresh token should be equal")
                }

                else -> fail("Unexpected result: $result")
            }
        }
    }

    @Test
    fun `should refresh user authorization`() = runTest {
        withMockClient(MockEngine { request ->
            request.assertMethod(HttpMethod.Post)
            request.assertUrl("${config.baseUrl}/oauth/token")
            request.assertContentType(ContentType.Application.FormUrlEncoded)
            request.assertBody(
                TextContent(
                    "grant_type=refresh_token&refresh_token=refreshToken",
                    ContentType.Application.FormUrlEncoded
                )
            )
            respond(
                content = ByteReadChannel(
                    """
                    {
                        "access_token": "newAccessToken",
                        "refresh_token": "newRefreshToken",
                        "token_type": "tokenType",
                        "expires_in": 3600
                    }
                """.trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }) {
            when (val result = auth(it).reauthorize("refreshToken")) {
                is CallResult.Success -> {
                    val userTokens = result.data
                    assertEquals("newAccessToken", userTokens.accessToken, "Access token should be equal")
                    assertEquals("newRefreshToken", userTokens.refreshToken, "Refresh token should be equal")
                }

                else -> fail("Unexpected result: $result")
            }
        }
    }

    private fun auth(client: ApiClient): DefaultAuth = DefaultAuth(config, client)
}
