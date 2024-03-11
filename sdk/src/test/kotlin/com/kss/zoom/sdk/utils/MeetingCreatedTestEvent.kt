package com.kss.zoom.sdk.utils

val MEETING_CREATED_TEST_EVENT: String = """
{
  "event": "meeting.created",
  "event_ts": 123456789,
  "payload": {
    "account_id": "dzFrpLGqRcOtpiQQeAVDVA==",
    "operator": "operator@email.com",
    "operator_id": "operator_id",
    "object": {
      "uuid": "uuid",
      "id": 78723497365,
      "host_id": "lqkrEKqMR1CCmALIVs73RQ",
      "topic": "Meeting Topic",
      "type": 2,
      "start_time": "2024-02-21T08:49:58Z",
      "duration": 60,
      "timezone": "America/Los_Angeles",
      "join_url": "https://us04web.zoom.us/j/78723497365?pwd=67MuOC4RaEDM5wbu1NajMVnvadebNY.1",
      "password": "8yTzuF"
    }
  }
}
""".trimIndent()