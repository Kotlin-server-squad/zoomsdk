package com.kss.zoom.sdk.utils

val MEETING_PARTICIPANT_JOINED_TEST_EVENT = """
{
  "event": "meeting.participant_joined",
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
      "duration": 60,
      "participant": {
        "user_id": "user-id",
        "user_name": "user-name",
        "participant_uuid": "participant-uuid",
        "join_time": "2021-01-01T00:00:00Z",
        "email": "participant@email.com",
        "registrant_id": "registrant-id",
        "participant_user_id": "participant-user-id",
        "customer_key": "customer-key",
        "phone_number": "+1-123-456-7890"
      }
    }
  }
}
""".trimIndent()