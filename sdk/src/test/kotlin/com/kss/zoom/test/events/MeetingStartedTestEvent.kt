package com.kss.zoom.test.events

val MEETING_STARTED_TEST_EVENT = """
{
  "event": "meeting.started",
  "event_ts": 123456789,
  "payload": {
    "account_id": "account-id",
    "object": {
      "uuid": "meeting-uuid",
      "id": "1234567890",
      "host_id": "host-id",
      "topic": "Meeting Topic",
      "type": 2,
      "start_time": "2021-01-01T00:00:00Z",
      "timezone": "America/Los_Angeles",
      "duration": 60
    }
  }
}
""".trimIndent()