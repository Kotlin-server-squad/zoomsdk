package com.kss.zoom.module.users

import com.kss.zoom.client.ApiClient
import com.kss.zoom.common.storage.TokenStorage
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.ZoomModuleConfig
import com.kss.zoom.module.auth.Auth
import com.kss.zoom.module.users.model.*
import com.kss.zoom.module.users.model.api.UserResponse
import com.kss.zoom.module.users.model.api.toModel
import com.kss.zoom.test.*
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DefaultUsersTest {

    companion object {
        private const val USER_ID = "userId"
        private const val ACCESS_TOKEN = "accessToken"
        private val userResponse = UserResponse(
            id = USER_ID,
            email = "email",
            firstName = "firstName",
            lastName = "lastName",
            type = 1,
        )
    }

    @Test
    fun `should create a user`() = runTest {
        val createRequest = CreateRequest(
            userId = USER_ID,
            email = "email",
            firstName = "John",
            lastName = "Doe",
            displayName = "John Doe",
        )
        withMockClient(
            MockEngine { request ->
                request.assertMethod(HttpMethod.Post)
                request.assertUrl("https://api.zoom.us/v2/users")
                request.assertBearerAuth(ACCESS_TOKEN)
                request.assertContentType(ContentType.Application.Json)
                request.assertBodyAsJson(createRequest.toApi().toJson())
                respondJson(userResponse.toJson())
            }
        ) {
            when (val result = users(it).create(createRequest)) {
                is CallResult.Success -> {
                    assertEquals(userResponse.toModel(), result.data, "User should be equal")
                }

                else -> fail("Unexpected result: $result")
            }
        }
    }

    @Test
    fun `should update a user`() = runTest {
        val updateRequest = UpdateRequest(
            userId = USER_ID,
            firstName = "Travis",
            lastName = "Foe",
            company = "cOmPaNy",
        )
        val expectedUserResponse = userResponse.copy(
            firstName = updateRequest.firstName!!,
            lastName = updateRequest.lastName!!,
            company = updateRequest.company,
        )
        withMockClient(
            MockEngine { request ->
                when (request.method) {
                    HttpMethod.Patch -> {
                        request.assertUrl("https://api.zoom.us/v2/users/$USER_ID")
                        request.assertBearerAuth(ACCESS_TOKEN)
                        request.assertContentType(ContentType.Application.Json)
                        request.assertBodyAsJson(updateRequest.toApi().toJson())
                        respondOk()
                    }

                    HttpMethod.Get -> {
                        request.assertUrl("https://api.zoom.us/v2/users/$USER_ID")
                        request.assertBearerAuth(ACCESS_TOKEN)

                        respond(
                            content = ByteReadChannel(expectedUserResponse.toJson()),
                            status = HttpStatusCode.OK,
                            headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                        )
                    }

                    else -> fail("Unexpected request: $request")
                }
            }
        ) {
            users(it).update(updateRequest).let { result ->
                when (result) {
                    is CallResult.Success -> {
                        assertEquals(expectedUserResponse.toModel(), result.data, "User should be equal")
                    }

                    else -> fail("Unexpected result: $result")
                }
            }
        }
    }

    @Test
    fun `should get user details`() = runTest {
        withMockClient(
            MockEngine { request ->
                request.assertMethod(HttpMethod.Get)
                request.assertUrl("https://api.zoom.us/v2/users/$USER_ID")
                request.assertBearerAuth(ACCESS_TOKEN)
                respondJson(userResponse.toJson())
            }
        ) {
            users(it).get(GetRequest(userId = USER_ID)).let { result ->
                when (result) {
                    is CallResult.Success -> {
                        assertEquals(userResponse.toModel(), result.data, "User should be equal")
                    }

                    else -> fail("Unexpected result: $result")
                }
            }
        }
    }

    @Test
    fun `should delete user`() = runTest {
        withMockClient(
            MockEngine { request ->
                request.assertMethod(HttpMethod.Delete)
                request.assertBearerAuth(ACCESS_TOKEN)
                respondOk()
            }
        ) {
            users(it).delete(DeleteRequest(userId = USER_ID)).let { result ->
                when (result) {
                    is CallResult.Success -> {
                        // No data to assert
                    }

                    else -> fail("Unexpected result: $result")
                }
            }
        }
    }

    private fun users(
        client: ApiClient,
        auth: Auth = mock {},
        storage: TokenStorage = mock {
            everySuspend { getAccessToken(USER_ID) } returns ACCESS_TOKEN
        },
    ): DefaultUsers {
        return DefaultUsers(ZoomModuleConfig(), auth, storage, testClock, client)
    }
}
