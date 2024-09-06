package com.kss.zoom.model

import com.kss.zoom.model.context.DynamicContext

interface Model {
    val id: String
    val context: DynamicContext
}
