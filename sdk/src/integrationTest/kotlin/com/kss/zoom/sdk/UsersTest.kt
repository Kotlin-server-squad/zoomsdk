package com.kss.zoom.sdk

import com.kss.zoom.sdk.common.call
import com.kss.zoom.sdk.common.model.ZoomException
import com.kss.zoom.sdk.model.SharedObject
import com.kss.zoom.sdk.users.IUsers
import com.kss.zoom.sdk.users.model.api.pagination.UserPageQuery
import com.kss.zoom.sdk.users.model.domain.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.random.Random
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersTest : ZoomTestBase() {

    private lateinit var users: IUsers
    private lateinit var sharedObject: SharedObject

    @BeforeAll
    fun init() = runBlocking {
        val tempUsers = users()
//        clearUsers(tempUsers) // for testing purposes
        val user = createUser(tempUsers)
        sharedObject = SharedObject(user.id, user.email, user.type.value)
    }

    @BeforeEach
    fun setUp() = runBlocking {
        users = users()
    }

    @Test
    @Order(1)
    fun `create user and check if exists and delete them`() = runBlocking {
        val randomNumber = Random.nextLong(100000, 999999)
        val email = "kotlinserversquad+test$randomNumber@gmail.com"
        val request = CreateUser(
            email = email,
            firstName = "Mattie Holland",
            lastName = "Mario Melendez",
            displayName = "MarioMelendez",
            type = Type.BASIC,
            action = Action.CREATE
        )

        val createUserResponse = call {
            users.create(request)
        }
        assertNotNull(createUserResponse)

        call {
            users.delete(createUserResponse.id)
        }
    }

    @Test
    @Order(2)
    fun `get user`() = runBlocking {
        delay(1000)
        val response = call {
            users.get(sharedObject!!.customerId)
        }
        assertEquals(response.status, Status.PENDING)
        assertEquals(response.type, 1)
    }

    @Test
    @Order(3)
    fun `check user email`() = runBlocking {
        val response = call {
            users.checkEmail(sharedObject.email)
        }
        assertFalse { response }
    }

    @Test
    @Order(4)
    fun `update user`() = runBlocking {
        val phoneNumber = PhoneNumber(
            code = "+420", country = "Czech", label = Label.MOBILE, number = "444222333"
        )
        val updateUser = UpdateUser(
            company = "kotlin server squad",
            department = "marketing",
            jobTitle = "software engineer",
            language = "en-US",
            phoneNumbers = listOf(phoneNumber),
        )
        delay(1000)
        call {
            users.update(sharedObject.customerId, updateUser)
        }

        val response = call {
            users.get(sharedObject.customerId)
        }

        assertEquals(response.department, "marketing")
    }

    @Test
    @Order(7)
    fun `delete user`(): Unit = runBlocking {
        delay(1000)
        call {
            users.delete(sharedObject.customerId)
        }

        delay(1000)
        try {
            call {
                users.get(sharedObject.customerId)
            }
        } catch (e: ZoomException) {
            assertEquals(e.message, "Not found")
        }
    }

    @Test
    @Order(5)
    fun `get all user`() = runBlocking {
        val status = "pending"
        val pageSize = 10
        val items = call {
            users.list(UserPageQuery(pageNumber = 1, pageSize = pageSize, status = status))
        }.items

        assertTrue { items.any { it.email == sharedObject.email && it.type == sharedObject.type && it.status == status } }
    }

    @Test
    @Order(6)
    @Disabled("This test is disabled because it is not working, user has to be created and active")
    fun `get user permissions`() = runBlocking {
        val user = call {
            users.getUserPermissions(sharedObject.customerId)
        }
    }

    private suspend fun createUser(tempUsers: IUsers): User {
        val randomNumber = Random.nextLong(100000, 999999)
        val email = "kotlinserversquad+test$randomNumber@gmail.com"
        val request = CreateUser(
            email = email,
            firstName = "Mattie",
            lastName = "Melendez",
            displayName = "MattieMelendez",
            type = Type.BASIC,
            action = Action.CREATE
        )

        return call {
            tempUsers.create(request)
        }
    }

    private suspend fun clearUsers(tempUsers: IUsers) {
        val pageSize = 10
        var pendintUsers = call {
            tempUsers.list(UserPageQuery(pageNumber = 1, pageSize = pageSize, status = "pending"))
        }.items

        pendintUsers.forEach {
            it.id?.let {
                call {
                    tempUsers.delete(it)
                }
            }

        }

        val activeUsers = call {
            tempUsers.list(UserPageQuery(pageNumber = 1, pageSize = pageSize, status = "active"))
        }.items.filter { it.email != mainEmail }

        activeUsers.forEach {
            it.id?.let {
                call {
                    tempUsers.delete(it)
                }
            }
        }
    }

    companion object {
        val mainEmail = "kotlinserversquad@gmail.com"
    }
}
