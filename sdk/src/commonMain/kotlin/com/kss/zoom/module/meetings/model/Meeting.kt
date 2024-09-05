package com.kss.zoom.module.meetings.model

import com.kss.zoom.model.DynamicContext
import com.kss.zoom.model.Model

data class Meeting(
    override val id: String,
    override val context: DynamicContext = DynamicContext(),
    val uuid: String,
    val topic: String,
    val duration: Short,
    val hostId: String,
    val createdAt: Long,
    val startTime: Long,
    val timezone: String,
    val joinUrl: String,
    val status: String? = null,
    val hostEmail: String? = null,
    val startUrl: String? = null,
    val password: String? = null,
) : Model
