# Zoom SDK

**Table of Contents**
- [Overview](#overview)
- [Installation](#installation)
- [Usage](#usage)
  - [Initialization](#initialization)
  - [Authentication](#authentication)
  - [Exception Handling](#exception-handling)
  - [Dynamic Properties](#dynamic-properties)
  - [Meeting Management](#meeting-management)
  - [User Management](#user-management)
  - [Webhooks](#webhooks)
  - [Rate Limiting](#rate-limiting)
  - [Testing](#testing)
- [Supported Platforms](#supported-platforms)
- [Examples](#examples)
- [Contributing](#contributing)
- [License](#license)


## Overview
Zoom SDK is a tool for integrating Zoom meetings into your applications. 
It is available for both desktop and mobile applications.

**Benefits**
- **Authentication**: Handle user and server authentication with Zoom.
- **Meeting Management**: Create, and manage Zoom meetings. Gain access to meeting details and participants.
- **User Management**: Manage users within your Zoom account.
- **Webhooks**: Receive notifications about Zoom events and take action.
- **Rate Limiting**: Built-in rate limiting helps you avoid being blocked by Zoom.
- **Testing**: Built-in testing tools help you test your integration with Zoom.
- **Cross-Platform**: Available for both desktop and mobile applications.

## Installation
The SDK will be available on Maven Central. Stay tuned for the release announcement.

## Usage
Here is a quick guide to get you started with the Zoom SDK.

### Initialization
In a nutshell, the Zoom SDK allows you to:
```kotlin
// Instantiate the SDK
val zoom = Zoom.create("clientId", "clientSecret", "accountId")

// Access the Zoom API
val meetings = zoom.meetings()
val users = zoom.users()

// Work with the Zoom API
val meeting = call { meetings.get(GetRequest("userId1", "meetingId")) }
println("Found meeting: $meeting")

val user = call { users.get(GetRequest("userId1")) }
println("This is me: $user")
```

### Authentication
You can use the Zoom SDK on behalf of the user or on behalf of the server.

#### Server Authentication
You can use the Zoom SDK to authenticate the server with Zoom. This allows you to access Zoom resources 
in the respective Zoom account.

You need to register a Server-to-Server app in the Zoom Marketplace and obtain the client ID and client secret.

Next, please grant the following scopes (permissions) to the app:

**Meetings**

| Scope | Description |
| --- | --- |
|meeting:read:list_meetings:admin| View a user's meetings |
|meeting:read:list_past_instances:admin| View a user's meetings |
|meeting:read:meeting:admin| View a meeting |
|meeting:update:meeting:admin| Update a meeting |
|meeting:delete:meeting:admin| Delete a meeting |
|meeting:write:meeting:admin| Create a meeting for a user |

**Users**

| Scope | Description                |
| --- |----------------------------|
|user:read:list_users:admin| View users                 |
|user:read:user:admin| View a user                |
|user:delete:user:admin| Delete a user              |
|user:write:user:admin| Create a user           |
|user:update:user:admin|Update a user            |

In the Zoom app, please capture the following information:
- Client ID
- Client Secret
- Account ID

Once the app is in place you can authenticate the server with Zoom
and start using the Zoom SDK:
```kotlin
// Instantiate the SDK
val zoom = Zoom.create("clientId", "clientSecret", "accountId")

// Access the Zoom API
val meetings = zoom.meetings()
val users = zoom.users()
```

#### User Authentication
You can use the Zoom SDK to authenticate the user with Zoom. This allows you to access Zoom resources
on behalf of the user.

You need to register an OAuth app in the Zoom Marketplace 
and obtain the client ID and client secret.

Please grant the following scopes (permissions) to the app:

**Meetings**

TBD

| Scope | Description |
| --- | --- |


**Users**

TBD

| Scope | Description                |
| --- |----------------------------|

Once the app is in place you can authenticate the user with Zoom:
```kotlin
/**
 * OAuth2 flow: Requires user interaction
 */

// Helper method to get Zoom authorization URL
val authUrl = zoom.getAuthorizationUrl("callbackUrl")
println("Use $authUrl in the client code to obtain an authorization code")

// Once OAuth is done, we can authorize the SDK on behalf of a particular user using the authorization code
// We can authorize as many users as we want
zoom.authorize("userId1", "code1")
zoom.authorize("userId2", "code2")
zoom.authorize("userId3", "code3")

// We can refresh the authorization for a particular user
zoom.reauthorize("userId1")

// Alternatively, we can register a user directly with their access and refresh tokens
zoom.registerUser("userId4", "accessToken4", "refreshToken4")

// Access the Zoom API. All calls will be made on behalf of the user.
val meetings = zoom.meetings()
val users = zoom.users()
```

## Exception Handling
By default, the Zoom SDK does not throw exceptions. Instead, it returns a `CallResult` object that contains the result of the operation or an error.

You can check if the operation was successful or if an error occurred:

```kotlin
when (val result = meetings.get(GetRequest("userId1", "meetingId"))) {
    is CallResult.Success -> {
        val meeting = result.data
        println("Found meeting: $meeting")
    }
    is CallResult.Error -> {
        val error = result.message
        println("Error: $error")
    }
}
```
You can choose to handle a specific error:
```kotlin
when (val result = meetings.get(GetRequest("userId1", "meetingId"))) {
    is CallResult.Success -> {
        val meeting = result.data
        println("Found meeting: $meeting")
    }
    is CallResult.Error.NotFound -> {
        println("Meeting not found")
    }
    is CallResult.Error.Unauthorized -> {
        println("Unauthorized")
    }
    // Handle other errors
    else -> {
        println("Error: ${result.message}")
    }
}
```
If you prefer to handle exceptions, you can enable exceptions in the SDK:
```kotlin
// This will throw an IllegalStateException if an error occurs
val meeting = call { meetings.get(GetRequest("userId1", "meetingId")) }
```
## Dynamic Properties
The Zoom SDK allows you to access dynamic properties of Zoom resources. You can access these properties using the `context` parameter.

One of the challenges of working with Zoom is that the API responses can be quite large and complex. 
The Zoom SDK simplifies this by providing a way to access dynamic properties in a type-safe manner.

Example:
```kotlin
// A Meeting object has predefined fields, as well as a dynamic property called "context"
data class Meeting(
  // Identifies the meeting instance
  override val id: String,
  
  // Dynamic context
  override val context: DynamicContext = DynamicContext(),
  
  // Predefined fields
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
```

Suppose you're interested in the `participants` property of a meeting and, optionally, in a description. You can access it like this:

```kotlin
import com.kss.zoom.model.context.DynamicProperty.Companion.nullable
import com.kss.zoom.model.context.DynamicProperty.Companion.required

// Step 1: Define dynamic properties

// The field "participants" is required and it defaults to an empty list
val meetingParticipants = required<List<String>>("participants", emptyList())

// The field "description" is optional and remains null if not present
val description = nullable<String>("description")

// Step 2: Register a custom JSON serializer - do this once in your application
val meetingSerializer = MeetingSerializer(description, meetingParticipants)
// TODO register the serializer with the SDK
```

Now you can access the dynamic properties of a meeting:

```kotlin
// Scenario 1: Make a call to the Zoom API and access the dynamic properties
val meeting = call { meetings.get(GetRequest("userId1", "meetingId")) }
println(meeting.context[meetingParticipants])
println(meeting.context[description])
```

Another example is to access the `participants` property of a meeting in a webhook event:

```kotlin
// Scenario 2: Handle a webhook event and access the dynamic properties
// Incoming JSON from the webhook
val json = """
        {
            "id": "123",
            "uuid": "uuid",
            "topic": "topic",
            "duration": 60,
            "host_id": "hostId",
            "created_at": "2024-01-01T00:00:00Z",
            "start_time": "2024-01-01T00:00:00Z",
            "timezone": "timezone",
            "join_url": "joinUrl",
            "name": "Meeting",
            "description": "My test event",
            "schedule_type": 2,
            "participants": {
                "participant1": "participant-01@test.com",
                "participant2": "participant-02@test.com"
            }
        }
    """.trimIndent()
    val meeting = meetingSerializer.toModel(json)
    println(meeting.context[meetingParticipants])
```

## Meeting Management
The Zoom SDK allows you to create, update, and delete meetings. You can also access meeting details and participants.

TBD

## User Management
The Zoom SDK allows you to manage users within your Zoom account. You can create, update, and delete users. You can also access user details.

TBD

## Webhooks
The Zoom SDK allows you to receive notifications about Zoom events and take action. 
You can subscribe to webhooks and handle events in your application.

One of the challenges of working with webhooks is that the payload can be quite large and complex.
The Zoom SDK simplifies this by providing a way to access dynamic properties in a type-safe manner.

The basic idea is to define a custom JSON serializer that extracts the relevant information from the webhook payload.
You can then use this serializer to convert the JSON payload into a type-safe object that you can work with. 
The Zoom SDK provides a way to register custom JSON serializers in a simple and efficient manner.

Example:
```kotlin
// Use the `handler` DSL to define any custom fields and logic you need.
val webhookHandler = handler {
    // Define the fields you need from the webhook payload
    val uuid = add { required<String>("uuid") }
    val hostId = add { required<String>("host_id") }
    val agenda = add { nullable<String>("agenda") }
    val type = add { required<Int>("type", default = 1) }
    val duration = add { required<Int>("duration") }
  
    // Define custom logic to handle the webhook event
    on { event ->
        // Access the predefined fields
        println("Received meeting event: ${event.name}")
        println("Event timestamp: ${event.timestamp}")
      
        // Access the dynamic properties
        println("Meeting UUID: ${event.context[uuid]}")
        println("Host ID: ${event.context[hostId]}")
        event.context[agenda]?.let { println("Agenda: $it") }
        println("Meeting type: ${event.context[type]}")
        println("Meeting duration: ${event.context[duration]}")
      
        // Your own custom logic: Send a notification, make database updates, etc.
    }
}

// Use the HTTP client / framework you prefer to receive the incoming webhook request

// The request body of the incoming webhook parsed as a JSON string
val json = """
  {
    "event": "meeting.ended",
    "event_ts": 1658940994914,
    "payload": {
      "account_id": "d8u239ur932u39u2",
      "operator": "email@example.com",
      "operator_id": "iX3c3weri9PPuiP3",
      "object": {
        "uuid": "Sdghwi7erUGDy7sud",
        "id": "123456789",
        "host_id": "js78su3jsj28su38",
        "topic": "My Meeting",
        "start_time": "2023-04-01T09:00:00Z",
        "duration": "sixty",
        "timezone": "America/Los_Angeles",
        "end_time": "2023-04-01T10:00:00Z",
        "agenda": "Discussing the product launch"
      }
    }
  }  
""".trimIndent()
val request = WebhookRequest(
    signature = "signature",    // Use the value of the X-Zoom-Signature header, or your own signature header
    timestamp = 1630000000,     // Use the value of the X-Zoom-Request-Timestamp header, or your own timestamp header
    body = json                 // The JSON payload of the incoming webhook
)

// Use the webhook handler to process incoming events
// The processing is asynchronous and non-blocking. No result is returned, the request is processed in the background.
webhookHandler.handle(request)
```
Sometimes you may need to handle complex custom types. You can define a custom JSON serializer to handle these types:

```kotlin
import kotlinx.serialization.*

// A custom data class in your code representing a Zoom recording.
// The only requirement is using kotlinx.serialization annotations.
@Serializable
data class Recording(
    @SerialName("file_type") val fileType: String,
    @SerialName("download_url") val downloadUrl: String,
    @SerialName("recording_start") val start: String,
    @SerialName("recording_end") val end: String,
)

handler {
  // Add a custom serializer to allow the handler to parse the JSON payload  
  val recordings = add { required<List<Recording>>("recording_files") }
    .withSerializer(ListSerializer(Recording.serializer()))
  
    on { event ->
      // Access the custom field as a list of Recording objects  
      event.context[recordings].forEach { recording ->
          println("Recording: ${recording.downloadUrl}")
      }
    }
}
```


## Rate Limiting
The Zoom SDK includes built-in rate limiting to help you avoid being blocked by Zoom. The SDK will automatically handle rate limiting for you.

TBD

## Testing
The Zoom SDK allows you to generate mock Zoom events to test your integration and ensure it works as expected.

TBD

## Supported Platforms
The Zoom SDK is available for the following platforms:
* JVM
* Desktop
* Android
* iOS

## Examples
Stay tuned for examples to help you get started with the Zoom SDK.

[//]: # (- [Java]&#40;#java&#41;)

[//]: # (- [Kotlin]&#40;#kotlin&#41;)

[//]: # (- [Android]&#40;#android&#41;)

[//]: # (- [iOS]&#40;#ios&#41;)

[//]: # (- [React Native]&#40;#react-native&#41;)

[//]: # (- [Flutter]&#40;#flutter&#41;)

## Contributing
We welcome contributions to the Zoom SDK. Please refer to the [contribution guidelines](CONTRIBUTING.md) for more information.

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
