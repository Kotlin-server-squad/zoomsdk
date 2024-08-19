package com.kss.zoom.module.meetings.model

import com.kss.zoom.model.request.UserRequest

data class DeleteRequest(
    override val userId: String,
    val meetingId: String
) : UserRequest
