# Zoom SDK

**Table of Contents**
- [Overview](#overview)
- [Installation](#installation)
- [Usage](#usage)
  - [Initialization](#initialization)
  - [Authentication](#authentication)
  - [Exception Handling](#exception-handling)
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
    is CallResult.NotFound -> {
        println("Meeting not found")
    }
}
```
If you prefer to handle exceptions, you can enable exceptions in the SDK:
```kotlin
// This will throw an exception if an error occurs
val meeting = call { meetings.get(GetRequest("userId1", "meetingId")) }
```

## Meeting Management
The Zoom SDK allows you to create, update, and delete meetings. You can also access meeting details and participants.

TBD

## User Management
The Zoom SDK allows you to manage users within your Zoom account. You can create, update, and delete users. You can also access user details.

TBD

## Webhooks
The Zoom SDK allows you to receive notifications about Zoom events and take action. You can subscribe to webhooks and handle events in your application.

TBD

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
