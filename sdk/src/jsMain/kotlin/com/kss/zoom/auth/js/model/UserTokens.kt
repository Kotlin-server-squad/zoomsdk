package com.kss.zoom.auth.js.model

@OptIn(ExperimentalJsExport::class)
@JsExport
data class UserTokens(val accessToken: String, val refreshToken: String? = null)
