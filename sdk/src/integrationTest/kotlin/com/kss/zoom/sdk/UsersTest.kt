package com.kss.zoom.sdk

import com.kss.zoom.sdk.users.IUsers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UsersTest : ZoomTestBase() {

    private lateinit var users: IUsers

    @BeforeEach
    fun setUp() {
        users = users()
    }

    @Test
    fun `should load`() {

    }
}