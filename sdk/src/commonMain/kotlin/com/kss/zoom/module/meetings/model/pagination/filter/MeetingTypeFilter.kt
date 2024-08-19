package com.kss.zoom.module.meetings.model.pagination.filter

import com.kss.zoom.model.pagination.filter.PageFilter

data class MeetingTypeFilter(val meetingType: MeetingType) : PageFilter {
    override fun toQueryString(): String {
        return "type=${meetingType.id}"
    }
}
