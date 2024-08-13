package com.kss.zoomsdk.model

data class Page<T>(
    val index: Short,
    val size: Short,
    val hasNext: Boolean,
    val items: List<T>
)
