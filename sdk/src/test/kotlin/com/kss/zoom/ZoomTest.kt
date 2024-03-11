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
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ZoomTest {

    companion object {
        private const val AUTHORIZATION_CODE = "code"
        private const val CLIENT_ID = "clientId"
        private const val CLIENT_SECRET = "clientSecret"
        private val BEARER_TOKEN = "$CLIENT_ID:$CLIENT_SECRET".encodeBase64()
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
                                    val body =
                                        if (request.body is TextContent) (request.body as TextContent).text else ""
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
    }

    @Test
    fun `should load`() {
        Zoom.create(CLIENT_ID, CLIENT_SECRET)
    }

    @Test
    fun `should load with custom http client`() {
        Zoom.create(CLIENT_ID, CLIENT_SECRET, httpClient = httpClient)
    }

    @Test
    fun `should create Authorization module`() {
        Zoom.create(CLIENT_ID, CLIENT_SECRET).auth()
    }

    @Test
    fun `should create Meetings module`(): Unit = runBlocking {
        val zoom = Zoom.create(CLIENT_ID, CLIENT_SECRET, httpClient = httpClient)
        val tokens = tokens(zoom.auth())
        zoom.meetings(tokens)
    }

    @Test
    fun `should create Users module`(): Unit = runBlocking {
        val zoom = Zoom.create(CLIENT_ID, CLIENT_SECRET, httpClient = httpClient)
        val tokens = tokens(zoom.auth())
        zoom.meetings(tokens)
    }

    private suspend fun tokens(auth: Authorization): UserTokens =
        auth.authorizeUser(AuthorizationCode(AUTHORIZATION_CODE)).getOrThrow()
}