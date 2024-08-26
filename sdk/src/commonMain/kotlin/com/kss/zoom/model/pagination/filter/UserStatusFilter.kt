package com.kss.zoom.model.pagination.filter

import com.kss.zoom.common.notBlank

data class UserStatusFilter(val status: String) : PageFilter {
    init {
        status.notBlank("status")
    }
    override fun toQueryString(): String {
        return "status=$status"
    }
}
