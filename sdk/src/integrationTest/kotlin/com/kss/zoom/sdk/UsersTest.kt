package com.kss.zoom.sdk

import com.kss.zoom.sdk.users.CreateUserRequest
import com.kss.zoom.sdk.users.UserInfo
import com.kss.zoom.sdk.users.Users
import com.kss.zoom.utils.call
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UsersTest : ZoomTestBase() {

    private lateinit var users: Users

    @BeforeEach
    fun setUp() {
        users = users()
    }

    @Test
    fun `create user`() = runBlocking {
        val request = CreateUserRequest(
            action = CreateUserRequest.Action.create, userInfo = UserInfo(
                email = "kotlinserversquad+test1@gmail.com",
                firstName = "Kotlin",
                lastName = "Server",
                displayName = "KotlinServer",
                type = 1
            )
        )

        val user = call {
            users.create(request)
        }

        println(user)

    }

    @Test
    fun `get user`() = runBlocking {
        val user = call {
            users.get("dNeRClc2Q1Ce-tT5c1kHAQ")
        }

        println(user)
    }

    @Test
    fun `get delete`() = runBlocking {
        val user = call {
            users.delete("dNeRClc2Q1Ce-tT5c1kHAQ")
        }

        println(user)

    }

    @Test
    fun `get all user`() = runBlocking {
        val users = call {
            users.list()
        }
        users.forEach(::println)

    }
}