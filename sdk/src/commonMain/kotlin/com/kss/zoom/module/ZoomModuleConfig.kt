package com.kss.zoom.module

data class ZoomModuleConfig(val baseUrl: String = "https://api.zoom.us/v2") {
    init {
        require(baseUrl.isNotBlank()) { "baseUrl must not be blank" }
    }
}
