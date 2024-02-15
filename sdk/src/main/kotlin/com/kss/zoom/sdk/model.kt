package com.kss.zoom.sdk

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient

interface ZoomModule

abstract class ZoomModuleBase(auth: UserTokens, client: WebClient) : ZoomModule {
    private var userTokens: UserTokens? = auth

}