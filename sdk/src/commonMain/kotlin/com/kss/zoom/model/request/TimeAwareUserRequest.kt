package com.kss.zoom.model.request

import kotlinx.datetime.Clock

interface TimeAwareUserRequest : UserRequest {
    fun validate(clock: Clock)
}
