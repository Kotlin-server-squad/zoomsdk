package com.kss.zoom.model.api

interface Model<T> {
    val id: T
    val data: Map<String, String>
}
