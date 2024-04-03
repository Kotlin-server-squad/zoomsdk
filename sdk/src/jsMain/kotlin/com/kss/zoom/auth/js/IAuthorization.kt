package com.kss.zoom.auth.js

import com.kss.zoom.auth.js.model.UserTokens
import kotlin.js.Promise


@OptIn(ExperimentalJsExport::class)
@JsExport
interface IAuthorization {
    /**
     * Generate an access token for the Zoom API. This is solely for Server-to-Server OAuth apps.
     * see [https://developers.zoom.us/docs/internal-apps/s2s-oauth/#generate-access-token]
     * @param accountId The account ID to generate the access token for.
     * @return The access token.
     */
    fun generateAccessToken(accountId: String): Promise<String>

    /**
     * Authorize a user with the given code and exchange it for a pair of access and refresh tokens.
     * @param code The code received from Zoom OAuth callback.
     * @return User authorization as a pair of access and refresh tokens.
     */
    fun authorizeUser(code: String): Promise<UserTokens>

    /**
     * Refresh the user authorization with the given refresh token.
     * @param refreshToken The refresh token received from Zoom OAuth callback.
     * @return Renewed user authorization as a pair of access and refresh tokens.
     */
    fun refreshUserAuthorization(refreshToken: String): Promise<UserTokens>

    /**
     * Get the authorization URL to redirect the user to.
     * @param callbackUrl The URL to redirect the user to after authorization.
     * @return The authorization URL.
     */
    fun getAuthorizationUrl(callbackUrl: String): String
}