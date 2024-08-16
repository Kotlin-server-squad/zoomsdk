package com.kss.zoom.module.auth.model

import com.kss.zoom.common.notBlank
import io.ktor.http.*

data class AuthConfig(val clientId: String, val clientSecret: String, val baseUrl: Url = Url("https://zoom.us")) {
    init {
        clientId.notBlank("clientId")
        clientSecret.notBlank("clientSecret")
    }
}
