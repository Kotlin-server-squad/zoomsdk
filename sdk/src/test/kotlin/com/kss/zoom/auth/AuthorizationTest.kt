package com.kss.zoom.auth

import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.auth.model.AccessToken
import com.kss.zoom.auth.model.AuthorizationCode
import com.kss.zoom.auth.model.RefreshToken
import com.kss.zoom.auth.model.UserTokens
import com.kss.zoom.test.utils.verifyFailure
import com.kss.zoom.sdk.common.call
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL

class AuthorizationTest {

    companion object {
        private const val AUTHORIZATION_CODE = "code"
        private const val CLIENT_ID = "clientId"
        private const val CLIENT_SECRET = "clientSecret"
        private val BEARER_TOKEN = "$CLIENT_ID:$CLIENT_SECRET".encodeBase64()
    }

    private lateinit var authorization: Authorization

    @BeforeEach
    fun setup() {
        val httpClient = HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json()
            }
            engine {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        "/oauth/token" -> {
                            when (
                                request.url.encodedQuery.contains("code=$AUTHORIZATION_CODE") &&
                                        request.headers.contains("Authorization", "Basic $BEARER_TOKEN")
                            ) {
                                true -> {
                                    respond(
                                        content = ByteReadChannel(
                                            """
                                                {
                                                    "access_token": "accessToken",
                                                    "refresh_token": "refreshToken",
                                                    "token_type": "bearer",
                                                    "expires_in": 3599
                                                }
                                            """.trimIndent()
                                        ),
                                        status = HttpStatusCode.OK,
                                        headers = headersOf(
                                            HttpHeaders.ContentType,
                                            ContentType.Application.Json.toString()
                                        )
                                    )
                                }

                                false -> {
                                    val body = if (request.body is TextContent) (request.body as TextContent).text else ""
                                    when (body) {
                                        "grant_type=refresh_token&refresh_token=refreshToken" -> {
                                            respond(
                                                content = ByteReadChannel(
                                                    """
                                                        {
                                                            "access_token": "newAccessToken",
                                                            "refresh_token": "newRefreshToken",
                                                            "token_type": "bearer",
                                                            "expires_in": 3599
                                                        }
                                                    """.trimIndent()
                                                ),
                                                status = HttpStatusCode.OK,
                                                headers = headersOf(
                                                    HttpHeaders.ContentType,
                                                    ContentType.Application.Json.toString()
                                                )
                                            )
                                        }

                                        else -> respondError(
                                            HttpStatusCode.Unauthorized,
                                            "Unauthorized access to the resource."
                                        )
                                    }
                                }
                            }
                        }

                        else -> error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
        }
        authorization = Authorization.create(
            AuthorizationConfig.create(CLIENT_ID, CLIENT_SECRET),
            httpClient
        )
    }

    @Test
    fun `should authorize user`() = runBlocking {
        verifyUserAuthorization(
            actual = call { authorization.authorizeUser(AuthorizationCode(AUTHORIZATION_CODE)) },
            expected = UserTokens(
                accessToken = AccessToken("accessToken", 3599L),
                refreshToken = RefreshToken("refreshToken")
            )
        )
    }

    @Test
    fun `should reject unauthorized access`(): Unit = runBlocking {
        verifyFailure(HttpStatusCode.Unauthorized.value, "Unauthorized access to the resource.") {
            authorization.authorizeUser(AuthorizationCode("invalidCode"))
        }
    }

    @Test
    fun `should refresh user authorization`() = runBlocking {
        verifyUserAuthorization(
            actual = call { authorization.refreshUserAuthorization(RefreshToken("refreshToken")) },
            expected = UserTokens(
                accessToken = AccessToken("newAccessToken", 3599L),
                refreshToken = RefreshToken("newRefreshToken")
            )
        )
    }

    @Test
    fun `should reject invalid refresh token`(): Unit = runBlocking {
        verifyFailure(HttpStatusCode.Unauthorized.value, "Unauthorized access to the resource.") {
            authorization.refreshUserAuthorization(RefreshToken("staleRefreshToken"))
        }
    }

    @Test
    fun `should return correct authorization url`() {
        val callbackUrl = URL("http://localhost:8080/callback")
        val expectedUrl =
            URL("https://zoom.us/oauth/authorize?response_type=code&client_id=clientId&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fcallback")
        assertEquals(expectedUrl, authorization.getAuthorizationUrl(callbackUrl))
    }

    private fun verifyUserAuthorization(actual: UserTokens, expected: UserTokens) {
        assertEquals(expected, actual, "Invalid user authorization")
    }
}
