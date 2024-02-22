package com.kss.zoom.sdk.meetings

import com.kss.zoom.auth.UserTokens
import com.kss.zoom.client.WebClient
import com.kss.zoom.sdk.ZOOM_API_URL
import com.kss.zoom.sdk.ZoomModule
import com.kss.zoom.sdk.ZoomModuleBase
import com.kss.zoom.sdk.users.UserId
import com.kss.zoom.toIsoString
import com.kss.zoom.toWebClient
import io.ktor.client.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

interface Meetings : ZoomModule {

    /**
     * Create a one-time meeting for the given user.
     * @param topic The topic of the meeting.
     * @param startTime The time the meeting will start.
     * @param duration The duration of the meeting in minutes.
     * @param timezone The timezone of the meeting.
     * @return The created meeting.
     */
    suspend fun create(
        userId: UserId,
        topic: String,
        startTime: LocalDateTime,
        duration: Short,
        timezone: TimeZone
    ): Result<Meeting>

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

    override suspend fun create(
        userId: UserId,
        topic: String,
        startTime: LocalDateTime,
        duration: Short,
        timezone: TimeZone
    ): Result<Meeting> {
        assert(duration > 0) { "Duration must be greater than 0" }
        return client.post<Http.MeetingResponse>(
            url = "$ZOOM_API_URL/users/$userId/meetings",
            token = userTokens!!.accessToken.value,
            contentType = WebClient.JSON_CONTENT_TYPE,
            body = Http.CreateOneTimeMeetingRequest(
                topic = topic,
                type = 2,
                startTime = startTime.toIsoString(),
                duration = duration,
                timezone = timezone.id
            )
        ).toModel()
    }

    override suspend fun listScheduled(): Result<List<Meeting>> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelScheduled(meetingId: Long): Result<Meeting> {
        TODO("Not yet implemented")
    }
}

private fun Result<Http.MeetingResponse>.toModel(): Result<Meeting> =
    this.map { response ->
        Meeting(
            id = response.id,
            uuid = response.uuid,
            topic = response.topic,
            host = MeetingHost(
                id = response.hostId,
                email = response.hostEmail
            ),
            startTime = ZonedDateTime.parse(response.startTime).toLocalDateTime(),
            duration = response.duration,
            joinUrl = response.joinUrl,
            startUrl = response.startUrl,
            password = response.password,
            createdAt = ZonedDateTime.parse(response.createdAt).toInstant(),
            timeZone = TimeZone.getTimeZone(response.timezone),
        )
    }

private object Http {
    @Serializable
    data class CreateOneTimeMeetingRequest(
        val topic: String,
        val type: Short,
        @SerialName("start_time") val startTime: String? = null,
        val duration: Short? = null,
        val timezone: String? = null
    )

    @Serializable
    data class MeetingResponse(
        val id: Long,
        val uuid: String,
        val topic: String,
        val duration: Short,
        @SerialName("host_id") val hostId: String,
        @SerialName("host_email") val hostEmail: String,
        val status: String,
        @SerialName("start_time") val startTime: String,
        val timezone: String,
        @SerialName("created_at") val createdAt: String,
        @SerialName("start_url") val startUrl: String,
        @SerialName("join_url") val joinUrl: String,
        val password: String
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
