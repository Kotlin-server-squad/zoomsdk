package com.kss.zoom.sdk.model

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface ZoomModule

abstract class ZoomModuleBase(userTokens: UserTokens, val client: WebClient) : ZoomModule {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)
    protected var userTokens: UserTokens? = userTokens
        get() = field ?: throw IllegalStateException("User tokens have not been set.")
}