package com.kss.zoom.common.storage

import com.kss.zoom.module.auth.model.UserTokens
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

class InMemoryTokenStorageTest {

    private lateinit var storage: InMemoryTokenStorage

    companion object {
        private const val USER_ID = "user1"
        private val config = InMemoryTokenStorageConfig(accessTokenExpiry = 1.seconds)
        private val userTokens = UserTokens(
            accessToken = "access1",
            refreshToken = "refresh1",
            tokenType = "Bearer",
            expiresIn = 3600,
            createdAt = 0
        )
    }

    @BeforeTest
    fun setUp() {
        storage = InMemoryTokenStorage(config)
    }

    @Test
    fun `should return null if no data stored for user`() = runTest {
        assertNull(storage.getAccessToken(USER_ID), "Should be null")
    }

    @Test
    fun `should store user tokens`() = runTest {
        storage.saveTokens(USER_ID, userTokens)
        assertEquals(storage.getAccessToken(USER_ID), userTokens.accessToken, "Should be equal")
        assertEquals(storage.getRefreshToken(USER_ID), userTokens.refreshToken, "Should be equal")
    }

    @Test
    fun `should delete user tokens`() = runTest {
        storage.saveTokens(USER_ID, userTokens)
        storage.deleteTokens(USER_ID)
        assertNull(storage.getAccessToken(USER_ID), "Should be null")
        assertNull(storage.getRefreshToken(USER_ID), "Should be null")
    }

    @Test
    fun `access token should expire`() = runBlocking {
        storage.saveTokens(USER_ID, userTokens)
        assertEquals(storage.getAccessToken(USER_ID), userTokens.accessToken, "Should be equal")
        delay(config.accessTokenExpiry + 1.seconds)
        assertNull(storage.getAccessToken(USER_ID), "Should be null")
    }

}
