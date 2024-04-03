package com.kss.zoom.sdk.common

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class CustomScope(context: CoroutineContext) : CoroutineScope {
    override val coroutineContext: CoroutineContext = context
}