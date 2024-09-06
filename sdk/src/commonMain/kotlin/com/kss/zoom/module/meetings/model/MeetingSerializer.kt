package com.kss.zoom.module.meetings.model

import com.kss.zoom.model.context.DynamicProperty
import com.kss.zoom.model.serialization.ModelSerializer
import com.kss.zoom.module.meetings.model.api.MeetingResponse
import com.kss.zoom.module.meetings.model.api.toModel
import com.kss.zoom.model.api.ModelSerializer as ApiModelSerializer

class MeetingSerializer(
    vararg property: DynamicProperty<*>,
) : ModelSerializer<MeetingResponse, Meeting>(*property) {

    override val serializer: ApiModelSerializer<MeetingResponse> =
        ApiModelSerializer(
            MeetingResponse.serializer(),
            setOf(
                "uuid",
                "topic",
                "duration",
                "host_id",
                "created_at",
                "start_time",
                "timezone",
                "join_url",
                "status",
                "host_email",
                "start_url",
                "password",
            ),
            context,
        )

    override fun toModel(json: String): Meeting = toApiModel(json).toModel(context)
}
