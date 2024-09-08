package com.kss.zoom.test.integration

import com.kss.zoom.Zoom
import com.kss.zoom.model.context.DynamicContext
import kotlin.test.BeforeTest

abstract class ZoomModuleTest {
    companion object {
        val emptyContext = DynamicContext()
    }
    private lateinit var zoom: Zoom

    protected abstract fun setUp(zoom: Zoom)

    protected val userId = getPropertyOrThrow("ZOOM_USER_ID")

    @BeforeTest
    fun setUp() {
        // The below should be credentials from a Server-to-Server app in the respective Zoom account
        zoom = Zoom.create(
            clientId = getPropertyOrThrow("ZOOM_CLIENT_ID"),
            clientSecret = getPropertyOrThrow("ZOOM_CLIENT_SECRET"),
            accountId = getPropertyOrThrow("ZOOM_ACCOUNT_ID"),
        )
        setUp(zoom)
    }

    private fun getPropertyOrThrow(key: String): String {
        return System.getenv(key) ?: throw IllegalArgumentException("Missing required property: $key")
    }
}
