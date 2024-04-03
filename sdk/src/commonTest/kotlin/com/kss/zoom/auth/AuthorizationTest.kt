package com.kss.zoom.auth

import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.auth.model.*
import com.kss.zoom.client.WebClient
import com.kss.zoom.mockEngine
import com.kss.zoom.sdk.common.call
import com.kss.zoom.sdk.common.model.Url
import com.kss.zoom.verifyFailure
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorizationTest {
    companion object {
        private const val AUTHORIZATION_CODE = "code"
        private val clientId = ClientId("clientId")
        private val clientSecret = ClientSecret("clientSecret")
        private val BEARER_TOKEN = "${clientId.value}:${clientSecret.value}".encodeBase64()
        private val baseUrl = Url("https://zoom.us")
    }

    private val authorization: IAuthorization

    init {
        val httpClient = HttpClient(mockEngine(requestHandler(AUTHORIZATION_CODE, BEARER_TOKEN))) {
            install(ContentNegotiation) {
                json()
            }
        }
        authorization = Authorization(
            config = AuthorizationConfig(
                clientId = clientId,
                clientSecret = clientSecret,
                baseUrl = baseUrl
            ),
            client = WebClient(httpClient)
        )
    }

    @Test
    fun shouldAuthorizeUser() = runTest {
        verifyUserAuthorization(
            actual = call { authorization.authorizeUser(AuthorizationCode(AUTHORIZATION_CODE)) },
            expected = UserTokens(
                accessToken = AccessToken("accessToken", 3599L),
                refreshToken = RefreshToken("refreshToken")
            )
        )
    }

    @Test
    fun shouldRejectUnauthorizedAccess() = runTest {
        verifyFailure(HttpStatusCode.Unauthorized.value, "Unauthorized access to the resource.") {
            authorization.authorizeUser(AuthorizationCode("invalidCode"))
        }
    }

    @Test
    fun shouldRefreshUserAuthorization() = runTest {
        verifyUserAuthorization(
            actual = call { authorization.refreshUserAuthorization(RefreshToken("refreshToken")) },
            expected = UserTokens(
                accessToken = AccessToken("newAccessToken", 3599L),
                refreshToken = RefreshToken("newRefreshToken")
            )
        )
    }

    @Test
    fun shouldRejectInvalidRefreshToken() = runTest {
        verifyFailure(HttpStatusCode.Unauthorized.value, "Unauthorized access to the resource.") {
            authorization.refreshUserAuthorization(RefreshToken("staleRefreshToken"))
        }
    }

    @Test
    fun shouldReturnCorrectAuthorizationUrl() {
        val callbackUrl = Url("http://localhost:8080/callback")
        val expectedUrl =
            Url("https://zoom.us/oauth/authorize?response_type=code&client_id=clientId&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fcallback")
        assertEquals(expectedUrl, authorization.getAuthorizationUrl(callbackUrl))
    }

    private fun verifyUserAuthorization(actual: UserTokens, expected: UserTokens) {
        assertEquals(expected, actual, "Invalid user authorization")
    }
}
