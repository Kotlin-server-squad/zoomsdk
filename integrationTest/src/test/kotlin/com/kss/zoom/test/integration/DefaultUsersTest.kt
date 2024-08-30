package com.kss.zoom.test.integration

import com.kss.zoom.Zoom
import com.kss.zoom.common.call
import com.kss.zoom.model.CallResult
import com.kss.zoom.module.users.Users
import com.kss.zoom.module.users.model.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class DefaultUsersTest : ZoomModuleTest() {

    private lateinit var users: Users

    private val testUsers = mutableListOf<String>()

    override fun setUp(zoom: Zoom) {
        users = zoom.users()
    }

    @AfterTest
    fun tearDown() = runTest {
        testUsers.forEach {
            // Delete all created test users after each test
            users.delete(DeleteRequest((it)))
        }
    }

    @Test
    fun `should create a user`() = runTest {
        val request = createRequest()
        val user = call { users.create(request) }
        testUsers.add(user.id)
        verifyUser(user, request)
    }

    @Test
    fun `should update a user`() = runTest {
        // Pending users (newly created users) cannot be verified since Zoom won't return their details
        // Zoom is weird like that: https://devforum.zoom.us/t/get-user-by-email-returns-weird-results-for-out-of-account-users/57570
        val department = randomUUID()
        val request = updateRequest(userId, department)
        val updatedUser = call { users.update(request) }
        verifyUser(updatedUser, request)
    }

    @Test
    fun `should get a user`() = runTest {
        val user = call { users.get(GetRequest(userId)) }
        verifyUser(user)
    }

    @Test
    fun `should delete a user`() = runTest {
        val user = call { users.create(createRequest()) }
        call { users.delete(deleteRequest(user.id)) }
        when (val result = users.get(GetRequest(user.id))) {
            is CallResult.Success -> fail("Meeting should not be found")
            is CallResult.Error.NotFound -> {
                // Expected
            }
            else -> fail("Unexpected result: $result")
        }
    }

    private fun createRequest(displayName: String = "Test User"): CreateRequest {
        return CreateRequest(
            userId = userId,
            email = "test-${randomUUID()}@test.com",   // Random email to avoid conflicts
            firstName = "Test",
            lastName = "User",
            displayName = displayName
        )
    }

    private fun updateRequest(userId: String, department: String?): UpdateRequest {
        return UpdateRequest(
            userId = userId,
            department = department
        )
    }

    private fun deleteRequest(userId: String): DeleteRequest {
        return DeleteRequest(userId)
    }

    private fun verifyUser(user: User, request: CreateRequest) {
        assertEquals(request.email, user.email, "Email should match")
        assertEquals(request.firstName, user.firstName, "First name should match")
        assertEquals(request.lastName, user.lastName, "Last name should match")
        assertEquals(request.displayName, user.displayName, "Display name should match")
    }

    private fun verifyUser(user: User, request: UpdateRequest) {
        assertEquals(request.userId, user.id, "User ID should match")
        request.firstName?.let { assertEquals(it, user.firstName, "First name should match") }
        request.lastName?.let { assertEquals(it, user.lastName, "Last name should match") }
        request.company?.let { assertEquals(it, user.company, "Company name should match") }

    }

    private fun verifyUser(user: User) {
        assertTrue(user.id.isNotBlank(), "User ID should not be blank")
        assertTrue(user.email?.isNotBlank() ?: false, "User email should not be blank")
        assertTrue(user.firstName.isNotBlank(), "User first name should not be blank")
        assertTrue(user.lastName.isNotBlank(), "User last name should not be blank")
        assertTrue(user.displayName.isNotBlank(), "User display name should not be blank")
    }
}
