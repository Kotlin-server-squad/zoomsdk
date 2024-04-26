package com.kss.zoom.sdk.users

import com.kss.zoom.sdk.ModuleTest
import com.kss.zoom.sdk.common.call
import com.kss.zoom.sdk.users.model.api.*
import com.kss.zoom.sdk.users.model.api.UserInfo
import com.kss.zoom.sdk.users.model.domain.*
import com.kss.zoom.sdk.users.model.pagination.PaginationObject
import com.kss.zoom.sdk.users.model.pagination.UserPageQuery
import com.kss.zoom.sdk.users.model.pagination.toDomain
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersTest : ModuleTest<IUsers>() {

    @Test
    fun shouldCreateUser() = runTest {
        val createUserRequest = CreateUser(
            email = "jchill@example.com",
            firstName = "Jill",
            lastName = "Chill",
            displayName = "Jill Chill",
            type = UserType.BASIC,
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

        val response = call {
            users(responseJson).create(createUserRequest)
        }
        val expectedResponse = parseJson<CreateUserResponse>(responseJson).toDomain()
        assertEquals(expectedResponse, response, "Response should be equal to expected response")
    }

    @Test
    fun shouldUpdateUser() = runTest {
        val phoneNumber = PhoneNumber(
            code = "+420",
            country = "CZ",
            label = Label.MOBILE,
            number = "111222333",
            verified = null
        )
        val expectedUpdate = UpdateUser(
            company = "c1",
            department = "d1",
            firstName = "test",
            lastName = "dest",
            jobTitle = "dev",
            language = "english",
            phoneNumbers = listOf(phoneNumber),
            personalMeetingId = 1234567890L
        )

        call { users().update("test", expectedUpdate) }
        val request = assertRequestSent("/v2/users/test", HttpMethod.Patch)
        val update = parseJson<UpdateUserRequest>(request).toDomain()
        assertEquals(expectedUpdate, update, "Update should be equal to expected update")
    }

    @Test
    fun shouldGetUser() = runTest {
        val responseJson = """
           {
           "email": "jchill@example.com",
           "first_name": "Jill",
           "last_name": "Chill",
           "type": 1
           }
        """.trimIndent()
        val response = call { users(responseJson).get("test") }
        val expectedResponse = parseJson<UserInfo>(responseJson).toDomain()
        assertEquals(expectedResponse, response, "Response should be equal to expected response")
    }

    @Test
    fun shouldCheckUsersEmail() = runTest {
        val responseJson = """
           {
           "existed_email": true
           }
        """.trimIndent()
        val response = call { users(responseJson).checkEmail("test@test.com") }
        assertEquals(true, response, "Response should be equal to true")
    }

    @Test
    fun shouldGetUsersPermission() = runTest {
        val responseJson = """
           {
            "permissions": [
                "Disclaimers:Read",
                "ZoomDevelopersOAuth:Read",
                "WSCalendarIntegration:Read",
                "FeatureReleaseControls:Read",
                "IMGroups:Read",
                "BillingSubscription:Edit"
             ]
           }
        """.trimIndent()
        val response = call { users(responseJson).getUserPermissions("test") }
        val expectedResponse = parseJson<UserPermissionsResponse>(responseJson).toDomain()
        assertEquals(expectedResponse, response, "Response should be equal to expected response")
    }

    @Test
    fun shouldDeleteUser() = runTest {
        call { users().delete("test") }
        assertRequestSent("/v2/users/test", HttpMethod.Delete)
    }

    @Test
    fun shouldListUsers() = runTest {
        val responseJson = """
            {
               "page_count":2,
               "page_number":1,
               "page_size":3,
               "total_records":5,
               "users":[
                  {
                     "email":"jchill@example.com",
                     "first_name":"Jill",
                     "last_name":"Chill",
                     "type":1,
                     "status": "active"
                  },
                  {
                     "email":"jdoe@example.com",
                     "first_name":"Jane",
                     "last_name":"Doe",
                     "type":1,
                     "status": "active"
                  },
                  {
                     "email":"fzane@example.com",
                     "first_name":"Franke",
                     "last_name":"Zane",
                     "type":1,
                     "status": "active"
                  }
               ]
            }
        """.trimIndent()
        val query = UserPageQuery(
            pageNumber = 1,
            pageSize = 3
        )
        val response = call { users(responseJson).list(query) }
        val expectedResponse = parseJson<PaginationObject>(responseJson).toDomain()
        assertEquals(expectedResponse, response, "Response should be equal to expected response")
    }

    private fun users(responseBody: String? = null): IUsers =
        module(responseBody) { zoom, tokens ->
            zoom.users(tokens)
        }
}