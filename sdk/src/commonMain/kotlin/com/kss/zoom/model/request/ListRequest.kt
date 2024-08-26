package com.kss.zoom.model.request

data class ListRequest(
    override val userId: String,
    val pageRequest: PageRequest
) : UserRequest
