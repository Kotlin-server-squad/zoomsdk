package com.kss.zoom.sdk.utils

val MEETING_ENDED_TEST_EVENT = """
{
  "event": "meeting.ended",
  "event_ts": 123456789,
  "payload": {
    "account_id": "account-id",
    "object": {
      "uuid": "meeting-uuid",
      "id": "1234567890",
      "host_id": "host-id",
      "topic": "Meeting Topic",
      "type": 2
    }
  }
}
""".trimIndent()