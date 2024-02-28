package com.kss.zoom.sdk

import com.kss.zoom.Zoom
import com.kss.zoom.auth.AccountId
import com.kss.zoom.auth.UserTokens
import com.kss.zoom.utils.callSync

abstract class ZoomTestBase {
    companion object {
        private val CLIENT_ID: String = System.getenv("CLIENT_ID")
        private val CLIENT_SECRET: String = System.getenv("CLIENT_SECRET")
        private val ACCOUNT_ID: String = System.getenv("ACCOUNT_ID")
    }

    private val zoom = Zoom.create(
        clientId = CLIENT_ID,
        clientSecret = CLIENT_SECRET
    )

    private val userTokens = userTokens()

    fun meetings(): Meetings =
        zoom.meetings(userTokens)

    fun users(): Users =
        zoom.users(userTokens)

    private fun userTokens(): UserTokens {
        val accountId = AccountId(ACCOUNT_ID)
        return UserTokens(
            accessToken = callSync { zoom.auth().generateAccessToken(accountId) }
        )
    }
}