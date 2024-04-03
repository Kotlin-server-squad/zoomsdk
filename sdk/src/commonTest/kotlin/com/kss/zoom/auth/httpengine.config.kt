package com.kss.zoom.auth

import io.ktor.client.request.*
import io.ktor.http.content.*

fun requestHandler(code: String, token: String): (HttpRequestData) -> String? = { request ->
    when (request.url.encodedPath) {
        "/oauth/token" -> {
            when (
                request.url.encodedQuery.contains("code=$code") &&
                        request.headers.contains("Authorization", "Basic $token")
            ) {
                true -> {
                    """
                        {
                            "access_token": "accessToken",
                            "refresh_token": "refreshToken",
                            "token_type": "bearer",
                            "expires_in": 3599
                        }
                    """.trimIndent()

                }

                false -> {
                    val body = if (request.body is TextContent) (request.body as TextContent).text else ""
                    when (body) {
                        "grant_type=refresh_token&refresh_token=refreshToken" -> {
                            """
                                {
                                    "access_token": "newAccessToken",
                                    "refresh_token": "newRefreshToken",
                                    "token_type": "bearer",
                                    "expires_in": 3599
                                }
                            """.trimIndent()
                        }

                        else -> null
                    }
                }
            }
        }

        else -> error("Unhandled ${request.url.encodedPath}")
    }
}