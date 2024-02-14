package com.kss.zoom.sdk.users

import com.kss.zoom.ZoomModule
import com.kss.zoom.auth.Authorization

interface Users : ZoomModule {
}

class UsersImpl private constructor(private val auth: Authorization) : Users {
    override fun auth(): Authorization = auth

    companion object {
        fun create(authorization: Authorization): Users = UsersImpl(authorization)
    }
}
