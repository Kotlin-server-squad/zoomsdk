package com.kss.zoom.sdk.common

const val ZOOM_API_URL = "https://api.zoom.us/v2"

object SupportedEvents {
    object Meeting {
        const val CREATED = "meeting.created"
        const val STARTED = "meeting.started"
        const val ENDED = "meeting.ended"
        const val PARTICIPANT_JOINED = "meeting.participant_joined"
        const val PARTICIPANT_LEFT = "meeting.participant_left"
    }
}
