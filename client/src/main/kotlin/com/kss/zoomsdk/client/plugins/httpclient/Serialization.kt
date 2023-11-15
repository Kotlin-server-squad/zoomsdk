package com.kss.zoomsdk.client.plugins.httpclient

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun HttpClientConfig<*>.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}