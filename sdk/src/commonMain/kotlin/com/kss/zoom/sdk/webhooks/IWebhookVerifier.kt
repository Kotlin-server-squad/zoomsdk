package com.kss.zoom.sdk.webhooks

import com.kss.zoom.sdk.webhooks.model.Request

interface IWebhookVerifier {
    suspend fun verify(request: Request): Result<String>

    suspend fun verify(payload: String, timestamp: Long?, signature: String?): Result<String>
}