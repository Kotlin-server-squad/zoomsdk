package com.kss.zoom.model.pagination.filter

import com.kss.zoom.common.extensions.toIsoDateTimeString
import kotlinx.datetime.TimeZone

data class DateTimeFilter(val from: Long, val to: Long, val timezone: String = TimeZone.UTC.id) : PageFilter {
    init {
        require(from <= to) { "from must be less than or equal to to" }
    }

    override fun toQueryString(): String {
        return "from=${from.toIsoDateTimeString(timezone)}&to=${to.toIsoDateTimeString(timezone)}"
    }
}
