package com.kss.zoom.model

import com.kss.zoom.common.greaterZero

data class PageRequest(
    val index: Short,
    val size: Short
) {
    init {
        index.greaterZero("index")
        size.greaterZero("size")
    }
}
