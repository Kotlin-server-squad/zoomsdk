# Zoom SDK

## Overview
This is a Kotlin SDK for the [Zoom API](https://marketplace.zoom.us/docs/api-reference/introduction). 
It provides a simple way to make API calls to Zoom.

## Benefits
* Provides a simple way to access Zoom's features programmatically.
* Handles the OAuth authentication process and makes authenticated requests to the Zoom API.
* Highly flexible and customizable. Use as much or as little as you need.

## SDK Modules
* [Authorization](authorization/README.md)
* [Meetings](meetings/README.md)

## How to Use It?

```kotlin
import com.kss.zoomsdk.Zoom

// Create a Zoom SDK instance
val zoom = Zoom.create(
    clientId = "your-client-id",
    clientSecret = "you-client-secret"
)

// Get the authorization URL
val authUrl = zoom.auth().getAuthorizationUrl("https://your-redirect-url")

// Redirect the user to the authorization URL
// Once the user authorizes your app, Zoom will redirect the user back to your app with an authorization code
val userAuthorization = zoom.auth().authorizeUser("your-authorization-code")

// Use the access token to make API calls
val meetings = zoom.meetings().listScheduled(userAuthorization.accessToken)

// Once the access token expires, use the refresh token to get a new pair of tokens
val refreshedUserAuthorization = zoom.auth().refreshUserAuthorization(userAuthorization.refreshToken)

// Store the new pair of tokens and keep using them to make API calls
```

## Installation
Add the following dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.kss.zoom:zoom-sdk:0.1.0")
}
```

## Prerequisites
In order to use the SDK, you need to have the following:
1. Register an account with [Zoom](https://zoom.us/)
2. Create an app in the [Zoom App Marketplace](https://marketplace.zoom.us/)
3. Enable OAuth authentication in the app, see [here](https://developers.zoom.us/docs/zoom-apps/authentication)

## HTTP Client
This SDK relies on [Ktor HTTP client](https://ktor.io/) to make API calls to Zoom.

The SDK ships with sensible defaults for the HTTP client, but you can provide your own HTTP client if you need to.

```kotlin
import com.kss.zoomsdk.Zoom
import io.ktor.client.engine.cio.CIO

// Create a custom HTTP client (read the Ktor documentation for more details)
val yourCustomHttpClient = HttpClient(CIO) {
    // Your custom configuration
}

// Create a Zoom SDK instance with a custom HTTP client
val zoom = Zoom.create(
    clientId = "your-client-id",
    clientSecret = "your-client-secret",
    httpClient = yourCustomHttpClient
)
```

## Modularity
The SDK is modular and you can use only the parts you need. 
For example, if you only need to make API calls to the Zoom Meetings API, 
you can create a Zoom SDK instance with only the Meetings module.

```kotlin
import com.kss.zoomsdk.Zoom
import com.kss.zoom.call

// Create a Zoom SDK to schedule and cancel meetings
val meetingsSDK = Zoom.meetings("clientId", "clientSecret")

// Authorize a user
val authUrl = meetingsSDK.auth().getAuthorizationUrl(URL("http://localhost:8080/callback"))

// Redirect the user to the authUrl and get the authorization code in the callback
val authCode = AuthorizationCode("code-sent-by-zoom")

// Authorize the user with the auth code
val userAuth = meetingsSDK.auth().authorizeUser(authCode).getOrThrow()

// List scheduled meetings of the user
meetingsSDK.authorize(userAuth)
val meetings = meetingsSDK.listScheduled().getOrThrow()

// Or use the fluent API
val meetings = meetingsSDK.authorize(userAuth).listScheduled().getOrThrow()

// The authorize method only needs to be called once. It attaches the user authorization to the SDK instance.
// You can then make multiple API calls without having to pass the user authorization again.
val meeting = meetingsSDK.getMeeting("meetingId").getOrThrow()
```

## Exception Handling
The SDK accommodates for various error scenarios and provides a way to handle them.

You can choose your preferred style of error handling.

### Functional Style

All API calls return a `kotlin.Result` type, which can be used to handle the success and failure cases.
It comes with a set of extension functions to make it easier to work with.

```kotlin

val result = meetingsSDK.listScheduled(userAuth.accessToken)
when {
    result.isSuccess -> {
        val scheduledMeetings = result.getOrThrow()
        println("Scheduled meetings: $scheduledMeetings")
    }

    result.isFailure -> {
        val exception = result.exceptionOrNull()
        println("Failed to list scheduled meetings: $exception")
    }
}
```

## Try-Catch Style
Tired of pattern matching, or a constant calls to `getOrThrow()`? You can use the good old try-catch style.

```kotlin

// This will throw a ZoomException if the API call fails
val scheduledMeetings = call { meetingsSDK.listScheduled(userAuth.accessToken) }

// Handle the happy path
```

## Building from Source Code
To build the SDK from source code, clone the repository and run the following command:

```shell
./gradlew build
```

## Contributing
To contribute to the SDK, please read the [CONTRIBUTING.md](CONTRIBUTING.md) file.
