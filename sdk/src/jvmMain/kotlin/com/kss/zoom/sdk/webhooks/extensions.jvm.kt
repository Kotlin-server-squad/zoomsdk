package com.kss.zoom.sdk.webhooks

import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils

actual fun String.hash(token: String): String =
    HmacUtils(HmacAlgorithms.HMAC_SHA_256, token).hmacHex(this)
