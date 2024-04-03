package com.kss.zoom.auth.js

import com.kss.zoom.auth.Authorization
import com.kss.zoom.auth.config.AuthorizationConfig
import com.kss.zoom.auth.js.model.UserTokens
import com.kss.zoom.auth.model.AccountId
import com.kss.zoom.auth.model.AuthorizationCode
import com.kss.zoom.auth.model.RefreshToken
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.common.CustomScope
import com.kss.zoom.sdk.common.call
import com.kss.zoom.sdk.common.model.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.promise
import kotlin.js.Promise

@OptIn(ExperimentalJsExport::class)
@JsExport
class Authorization(clientId: String, clientSecret: String) : IAuthorization {

    private val delegate = Authorization(
        AuthorizationConfig.create(clientId, clientSecret),
        WebClient()
    )

    private val mainScope = CustomScope(Dispatchers.Main)

    override fun generateAccessToken(accountId: String): Promise<String> {
        return mainScope.promise {
            call { delegate.generateAccessToken(AccountId(accountId)) }.value
        }
    }

    override fun authorizeUser(code: String): Promise<UserTokens> {
        return mainScope.promise {
            call { delegate.authorizeUser(AuthorizationCode(code)) }.let {
                UserTokens(it.accessToken.value, it.refreshToken?.value)
            }
        }
    }

    override fun refreshUserAuthorization(refreshToken: String): Promise<UserTokens> {
        return mainScope.promise {
            call { delegate.refreshUserAuthorization(RefreshToken(refreshToken)) }.let {
                UserTokens(it.accessToken.value, it.refreshToken?.value)
            }
        }
    }

    override fun getAuthorizationUrl(callbackUrl: String): String {
        return delegate.getAuthorizationUrl(Url(callbackUrl)).value
    }
}