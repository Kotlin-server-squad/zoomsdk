package com.kss.zoom.module.meetings.model.pagination.filter

/**
 * https://developers.zoom.us/docs/api/rest/reference/zoom-api/methods/#operation/meetings
 */
enum class MeetingType(val id: String) {
    Scheduled("scheduled"),
    Live("live"),
    Upcoming("upcoming"),
    UpcomingMeetings("upcoming_meetings"),
    PreviousMeetings("previous_meetings")
}
