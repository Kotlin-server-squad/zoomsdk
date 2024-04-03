package com.kss.zoom

import com.kss.zoom.auth.js.Authorization
import com.kss.zoom.auth.js.IAuthorization

@JsModule("@js-joda/timezone")
@JsNonModule
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("ZoomJs")
class ZoomJs(clientId: String, clientSecret: String) {
    private val authorization: IAuthorization = Authorization(clientId, clientSecret)

    fun auth(): IAuthorization = authorization
}
