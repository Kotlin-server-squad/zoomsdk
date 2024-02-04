package com.kss.zoom.auth

import com.kss.zoom.auth.config.AuthorizationConfig
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL

class AuthorizationTest {

    companion object {
        private const val AUTHORIZATION_CODE = "code"
    }

    private lateinit var authorization: IAuthorization

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
                            when (request.headers.contains("Authorization", "Bearer $AUTHORIZATION_CODE")) {
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
                                    when ((request.body as TextContent).text) {
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
            AuthorizationConfig.create("clientId", "clientSecret"),
            httpClient
        )
    }

    @Test
    fun `should authorize user`() = runBlocking {
        verifyUserAuthorization(
            actual = authorization.authorizeUser(AuthorizationCode(AUTHORIZATION_CODE)),
            expected = UserAuthorization(
                accessToken = AccessToken("accessToken", 3599L),
                refreshToken = RefreshToken("refreshToken")
            )
        )
    }

    @Test
    fun `should reject unauthorized access`() {
        assertThrows(AuthorizationException::class.java) {
            runBlocking {
                authorization.authorizeUser(AuthorizationCode("invalidCode"))
            }
        }
    }

    @Test
    fun `should refresh user authorization`() = runBlocking {
        verifyUserAuthorization(
            actual = authorization.refreshUserAuthorization(RefreshToken("refreshToken")),
            expected = UserAuthorization(
                accessToken = AccessToken("newAccessToken", 3599L),
                refreshToken = RefreshToken("newRefreshToken")
            )
        )
    }

    @Test
    fun `should reject invalid refresh token`() {
        assertThrows(AuthorizationException::class.java) {
            runBlocking {
                authorization.refreshUserAuthorization(RefreshToken("staleRefreshToken"))
            }
        }
    }

    @Test
    fun `should return correct authorization url`() {
        val callbackUrl = URL("http://localhost:8080/callback")
        val expectedUrl = URL("https://zoom.us/oauth/authorize?response_type=code&client_id=clientId&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fcallback")
        assertEquals(expectedUrl, authorization.getAuthorizationUrl(callbackUrl))
    }

    private fun verifyUserAuthorization(actual: UserAuthorization, expected: UserAuthorization) {
        assertEquals(expected, actual, "Invalid user authorization")
    }
}
