package com.kss.zoom.model.pagination.filter

interface PageFilter {
    fun toQueryString(): String
}
