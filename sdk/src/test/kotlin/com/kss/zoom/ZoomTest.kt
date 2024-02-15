package com.kss.zoom

import com.kss.zoom.auth.Authorization
import com.kss.zoom.auth.AuthorizationCode
import com.kss.zoom.auth.UserTokens
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ZoomTest {

    companion object {
        private const val AUTHORIZATION_CODE = "code"
    }

    private lateinit var httpClient: HttpClient

    @BeforeEach
    fun setup() {
        httpClient = HttpClient(MockEngine) {
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
    }

    @Test
    fun `should load`() {
        Zoom.create("clientId", "clientSecret")
    }

    @Test
    fun `should load with custom http client`() {
        Zoom.create("clientId", "clientSecret", httpClient)
    }

    @Test
    fun `should create Authorization module`() {
        Zoom.create("clientId", "clientSecret").auth()
    }

    @Test
    fun `should create Meetings module`(): Unit = runBlocking {
        val zoom = Zoom.create("clientId", "clientSecret", httpClient)
        val tokens = tokens(zoom.auth())
        zoom.meetings(tokens)
    }

    @Test
    fun `should create Users module`(): Unit = runBlocking {
        val zoom = Zoom.create("clientId", "clientSecret", httpClient)
        val tokens = tokens(zoom.auth())
        zoom.meetings(tokens)
    }

    private suspend fun tokens(auth: Authorization): UserTokens =
        auth.authorizeUser(AuthorizationCode(AUTHORIZATION_CODE)).getOrThrow()
}