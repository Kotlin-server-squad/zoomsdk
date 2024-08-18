package com.kss.zoom.test

import com.kss.zoom.client.ApiClient
import dev.mokkery.mock

suspend fun <T> withMockClient(mockClient: ApiClient = mock {}, block: suspend (ApiClient) -> T) {
    block(mockClient)
}
