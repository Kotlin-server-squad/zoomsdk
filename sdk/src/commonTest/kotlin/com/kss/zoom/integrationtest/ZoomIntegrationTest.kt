package com.kss.zoom.integrationtest

import com.kss.zoom.Zoom
import com.kss.zoom.common.getPropertyOrThrow
import kotlin.test.BeforeTest

abstract class ZoomIntegrationTest {

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

}
