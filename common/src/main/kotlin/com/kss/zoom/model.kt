package com.kss.zoom

import com.kss.zoom.auth.Authorization
import com.kss.zoom.auth.UserAuthorization

class ZoomException(val code: Int, message: String) : Exception(message)

interface ZoomModule {

    fun auth(): Authorization
    fun authorize(userAuthorization: UserAuthorization)
}

abstract class ZoomModuleBase(private val auth: Authorization) : ZoomModule {
    private var userAuthorization: UserAuthorization? = null
    override fun auth(): Authorization = auth

    override fun authorize(userAuthorization: UserAuthorization) {
        this.userAuthorization = userAuthorization
    }
}