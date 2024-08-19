package com.kss.zoom.test

import com.kss.zoom.client.ApiClient
import dev.mokkery.mock

val testClock = TestClock()

suspend fun <T> withMockClient(mockClient: ApiClient = mock {}, block: suspend (ApiClient) -> T) {
    block(mockClient)
}
