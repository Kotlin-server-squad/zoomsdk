package com.kss.zoom.sdk.meetings.model.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recurrence(
    val type: Short,
    @SerialName("end_date_time") val endDateTime: String? = null,
    @SerialName("end_times") val endTimes: Short? = null,
    @SerialName("monthly_day") val monthlyDay: Short? = null,
    @SerialName("monthly_week") val monthlyWeek: Short? = null,
    @SerialName("monthly_week_day") val monthlyWeekDay: Short? = null,
    @SerialName("repeat_interval") val repeatInterval: Short? = null,
    @SerialName("weekly_days") val weeklyDays: List<Short>? = null
)