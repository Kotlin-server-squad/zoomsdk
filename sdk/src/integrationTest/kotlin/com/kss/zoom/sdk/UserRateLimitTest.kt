package com.kss.zoom.sdk

import com.kss.zoom.sdk.common.call
import com.kss.zoom.sdk.users.IUsers
import com.kss.zoom.sdk.users.model.api.pagination.UserPageQuery
import com.kss.zoom.sdk.users.model.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

class UserRateLimitTest : ZoomTestBase() {
    private lateinit var users: IUsers

    @BeforeEach
    fun setUp() {
        users = users()
    }

    @Test
    fun `test rate limit for create user`(): Unit = runBlocking {
        val users = users()
        val userDeferredList = List(10) { async { users.create(createUser()) } }
        val results = userDeferredList.awaitAll().map { it.getOrNull() }.filterNotNull()
        results.forEach {
            assertNotNull(it)
        }

        val allUsers = call {
            users.list(UserPageQuery(pageNumber = 1, pageSize = 10, status = Status.PENDING.value))
        }

        allUsers.items.forEach { println(it) }

        val deleteDeferredList = results.map { async { users.delete(it.id) } }
        deleteDeferredList.awaitAll()

    }

    @Test
    fun `test rate limit for create user with leaky bucket`(): Unit = runBlocking {
        val leakyBucket = LeakyBucket(1000, 4, users)
        val t = List(20) {
            async {
                leakyBucket.enqueueRequest(createUser())
            }
        }

        val s = t.awaitAll()
        s.forEach(::println)
    }

    suspend fun t(): User {
        return call { users.create(createUser()) }
    }

    @Test
    fun `test rate limit for create user with leaky bucket 1`(): Unit = runBlocking {
        val apiService = RateLimitedAPIService()

        // Launching a coroutine to handle responses
        val responseHandler = launch {
            apiService.collectResponses { response ->
                println("Processed response: $response")
            }
        }

        // Queueing tasks
        repeat(100) { taskId ->
            apiService.queueTask {
                apiService.simulateApiCall(taskId)
            }
        }

        // Wait for all tasks to be queued and processed
        delay(50000)
    }

    @Test
    fun `test rate limit with semaphore`(): Unit = runBlocking {
        val semaphore = Semaphore(4)
        repeat(20) {
            semaphore.withPermit {
                users.create(createUser())
            }
        }
    }

    @Test
    fun `test show all users in pending status`(): Unit = runBlocking {
        val allUsers = call {
            users.list(UserPageQuery(pageNumber = 1, pageSize = 100, status = Status.PENDING.value))
        }
        println("Total users: ${allUsers.totalRecords}")
        allUsers.items.forEach { println(it) }
    }

    private suspend fun createUser(): CreateUser {
        val randomNumber = Random.nextLong(100000, 999999)
        val email = "kotlinserversquad+test$randomNumber@gmail.com"
        return CreateUser(
            email = email,
            firstName = "Mattie",
            lastName = "Melendez",
            displayName = "MattieMelendez",
            type = Type.BASIC,
            action = Action.CREATE
        )
    }
}