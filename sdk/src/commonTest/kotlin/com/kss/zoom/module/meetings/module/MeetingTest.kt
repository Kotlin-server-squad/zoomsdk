package com.kss.zoom.module.meetings.module

import com.kss.zoom.module.meetings.model.Meeting
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MeetingTest {

    @Test
    fun `should add a custom field`() {
        val meeting = Meeting(
            id = "id",
            uuid = "uuid",
            topic = "topic",
            duration = 60,
            hostId = "hostId",
            createdAt = 0,
            startTime = 0,
            timezone = "timezone",
            joinUrl = "joinUrl",
        )
//            .addCustomField("key", "value")
//            .addCustomField("key2", 2)
//            .addCustomField("key3", 3.0)
//            .addCustomField("key4", true)

//        assertEquals(2, meeting.getCustomField<Int>("key2"))
//        assertNull(meeting.getCustomField<String>("non-existent-key"))
    }
}
