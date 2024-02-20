package com.kss.zoom.sdk

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient

interface ZoomModule

abstract class ZoomModuleBase(userTokens: UserTokens, val client: WebClient) : ZoomModule {
    protected var userTokens: UserTokens? = userTokens
        get() = field ?: throw IllegalStateException("User tokens have not been set.")

}