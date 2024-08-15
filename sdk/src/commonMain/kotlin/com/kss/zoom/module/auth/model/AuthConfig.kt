package com.kss.zoom.module.auth.model

import com.kss.zoom.common.notBlank

data class AuthConfig(val clientId: String, val clientSecret: String) {
    init {
        clientId.notBlank("clientId")
        clientSecret.notBlank("clientSecret")
    }
}
