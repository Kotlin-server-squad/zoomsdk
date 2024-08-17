package com.kss.zoom.module.auth

import com.kss.zoom.model.CallResult
import com.kss.zoom.module.auth.model.UserTokens

interface Auth {
    companion object {
        const val FORM_URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded"
    }

    fun getAuthorizationUrl(callbackUrl: String): String
    suspend fun authorize(code: String): CallResult<UserTokens>
    suspend fun reauthorize(refreshToken: String): CallResult<UserTokens>
}

