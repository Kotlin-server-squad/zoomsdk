package com.kss.zoom

import com.kss.zoom.auth.Authorization

class ZoomException(val code: Int, message: String) : Exception(message)

interface ZoomModule {
    fun auth(): Authorization
}