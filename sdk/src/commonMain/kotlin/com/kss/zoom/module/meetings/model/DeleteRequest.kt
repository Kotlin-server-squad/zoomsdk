package com.kss.zoom.module.meetings.model

data class DeleteRequest(
    val userId: String,
    val meetingId: String
)
