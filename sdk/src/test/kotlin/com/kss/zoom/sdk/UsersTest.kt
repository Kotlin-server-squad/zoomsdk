package com.kss.zoom.sdk

import com.kss.zoom.sdk.common.call
import com.kss.zoom.sdk.users.IUsers
import com.kss.zoom.sdk.users.model.api.CreateUserResponse
import com.kss.zoom.sdk.users.model.api.UpdateUserRequest
import com.kss.zoom.sdk.users.model.api.toDomain
import com.kss.zoom.sdk.users.model.domain.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UsersTest : ModuleTest<IUsers>() {


    override suspend fun sdkCall(module: IUsers): Any {
        TODO("Not yet implemented")
    }

    override fun module(): IUsers = users()

    private fun users(responseBody: String? = null): IUsers =
        module(responseBody) { zoom, tokens ->
            zoom.users(tokens)
        }

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun `should create user`(): Unit = runBlocking {
        val createUserRequest = CreateUser(
            email = "jchill@example.com",
            firstName = "Jill",
            lastName = "Chill",
            displayName = "Jill Chill",
            type = Type.BASIC,
            action = Action.CREATE
        )
        val responseJson = """
           {
           "email": "jchill@example.com",
           "first_name": "Jill",
           "id": "KDcuGIm1QgePTO8WbOqwIQ",
           "last_name": "Chill",
           "type": 1
           }
        """.trimIndent()

        val userResponse = call {
            users(responseJson).create(createUserRequest)
        }

        val expectedResponse = parseJson<CreateUserResponse>(responseJson).toDomain()
        assertEquals(expectedResponse.id, userResponse.id)
        assertEquals(expectedResponse.email, userResponse.email)
        assertEquals(expectedResponse.firstName, userResponse.firstName)
        assertEquals(expectedResponse.lastName, userResponse.lastName)
        assertEquals(expectedResponse.type, userResponse.type)
    }

    @Test
    fun `should update user`() {
        val phoneNumber = PhoneNumber(code = "+420", country = "CZ", label =Label.MOBILE, number = "111222333", verified = null)
        val updateUserRequest = UpdateUser(
            company = "c1",
            department = "d1",
            firstName = "test",
            lastName = "dest",
            jobTitle = "dev",
            language = "english",
            phoneNumbers = listOf(phoneNumber),
            personalMeetingId = 1234567890L
        )

        val responseJson = """
            {
            "company": "c1",
            "department": "d1",
            "first_name": "test",
            "id": "KDcuGIm1QgePTO8WbOqwIQ",
            "job_title": "dev",
            "language": "english",
            "last_name": "dest",
            "phone_numbers": [
                {
                "code": "+420",
                "country": "CZ",
                "label": "mobile",
                "number": "111222333",
                "verified": false
                }
            ],
            "personal_meeting_id": 1234567890
            }
        """.trimIndent()
    }

    @Test
    fun `should get user`() {
        TODO()
    }

    @Test
    fun `should check user's email`() {
        TODO()
    }

    @Test
    fun `should get user's permissions`() {
        TODO()
    }

    @Test
    fun `should delete user`() {
        TODO()
    }

    @Test
    fun `should list users`() {
        TODO()
    }
}