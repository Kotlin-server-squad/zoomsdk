package com.kss.zoom.auth.model

class ZoomException(val code: Int, message: String) : Exception(message)
