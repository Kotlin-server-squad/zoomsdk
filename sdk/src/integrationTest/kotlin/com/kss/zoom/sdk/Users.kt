package com.kss.zoom.sdk

import com.kss.zoom.sdk.users.Users
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Users : ZoomTestBase() {

    private lateinit var users: Users

    @BeforeEach
    fun setUp() {
        users = users()
    }

    @Test
    fun `should load`() {

    }
}