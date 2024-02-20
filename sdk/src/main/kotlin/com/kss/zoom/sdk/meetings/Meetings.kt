package com.kss.zoom.sdk.meetings

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.ZoomModule
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.toWebClient
import io.ktor.client.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Meetings : ZoomModule {

    /**
     * Create a meeting for the given user.
     * @param request Meeting parameters that will be used to create the meeting.
     * @return The created meeting.
     */
    suspend fun create(request: CreateMeetingRequest): Result<Meeting>
    suspend fun listScheduled(): Result<List<Meeting>>
    suspend fun cancelScheduled(meetingId: Long): Result<Meeting>
}

class MeetingsImpl private constructor(
    auth: UserTokens,
    client: WebClient
) : ZoomModuleBase(auth, client), Meetings {
    companion object {
        fun create(auth: UserTokens, httpClient: HttpClient? = null): Meetings =
            MeetingsImpl(auth, httpClient.toWebClient())
    }

    override suspend fun create(request: CreateMeetingRequest): Result<Meeting> {
        TODO()
//        client.post<Http.MeetingResponse>(
//            url = "/users/${request.userId}/meetings",
//            token = userTokens!!.accessToken,
//            contentType = WebClient.FORM_URL_ENCODED_CONTENT_TYPE,
//            body = "grant_type=account_credentials&account_id=${accountId.value}"
    }

    override suspend fun listScheduled(): Result<List<Meeting>> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelScheduled(meetingId: Long): Result<Meeting> {
        TODO("Not yet implemented")
    }
}

private object Http {

    @Serializable
    data class MeetingResponse(
        val id: Long,
        val agenda: String,
        val duration: Long,
        @SerialName("registration_url") val registrationUrl: String,
        @SerialName("join_url") val joinUrl: String,
        val password: String,
        val recurrence: Recurrence? = null
    )

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
}
