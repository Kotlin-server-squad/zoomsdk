package com.kss.zoom.sdk

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UsersTest : ZoomTestBase() {

    private lateinit var users: Users

    @BeforeEach
    fun setUp() {
        users = users()
    }

    @Test
    fun `should load`() {

    }
}