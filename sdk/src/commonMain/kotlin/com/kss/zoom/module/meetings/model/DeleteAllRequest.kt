package com.kss.zoom.module.meetings.model

import com.kss.zoom.common.notBlank

data class DeleteAllRequest(val userId: String, val nextPageToken: String? = null) {
    init {
        userId.notBlank("userId")
        nextPageToken?.notBlank("nextPageToken")
    }
}
