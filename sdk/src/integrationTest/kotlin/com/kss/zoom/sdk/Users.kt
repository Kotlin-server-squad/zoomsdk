package com.kss.zoom.sdk

import com.kss.zoom.sdk.users.Users
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Users {

    private lateinit var users: Users

    @BeforeEach
    fun setUp() {
        users = ZoomTestBase.users()
    }

    @Test
    fun `should load`() {

    }
}