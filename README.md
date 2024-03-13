# Zoom SDK
[![Gradle Build CI](https://github.com/Kotlin-server-squad/zoomsdk/actions/workflows/build.yaml/badge.svg)](https://github.com/Kotlin-server-squad/zoomsdk/actions/workflows/build.yaml)

## Overview
This is a Kotlin SDK for the [Zoom API](https://marketplace.zoom.us/docs/api-reference/introduction).
It provides a simple way to make API calls to Zoom.

## Benefits
* Provides a simple way to access Zoom's features programmatically.
* Handles the OAuth authentication process and makes authenticated requests to the Zoom API.
* Highly flexible and customizable. Use as much or as little as you need.

## How to Use It?

### Initialize the SDK

To use the SDK, you need to initialize it with your Zoom app credentials.
```kotlin
import com.kss.zoomsdk.Zoom

// Initialize the SDK with your Zoom app credentials
val zoom = Zoom.create("clientId", "clientSecret")
```

Once you have initialized the SDK, you can use it to make API calls to Zoom.

```kotlin
// Use the SDK to make API calls
val meetingSDK = zoom.meetings()

// This returns a list of scheduled meetings in a Result type
val result = meetingSDK.listScheduled("your-zoom-user-id")

// This returns a list of scheduled meetings in a non-blocking way
// It might fail with an exception
val scheduledMeetings = call { meetingSDK.listScheduled("your-zoom-user-id") }
```

### OAuth 2.0 Authentication
If you need to make authenticated requests to the Zoom API on behalf of a user, 
you need to go through the OAuth 2.0 authentication process.

```kotlin
import com.kss.zoomsdk.Zoom

// Initialize the Zoom SDK with your Zoom app credentials
val zoom = Zoom.create("clientId", "clientSecret")

// Use the authorization module to authorize a user
val auth = zoom.auth()

// Fetch the authorization URL using the callback URL you've registered in the Zoom App Marketplace
val authUrl = auth.getAuthorizationUrl("http://localhost:8080/callback")

// Redirect the user to the authUrl and get the authorization code in the callback
// This happens in your web application

// Authorize the user with the auth code
// The result is a pair of access and refresh tokens
val userTokens = call { auth.authorizeUser("code-sent-by-zoom") }

// Create an instance of Meetings SDK
val meetingSDK = zoom.meetings(userTokens)
```

## Installation
The SDK will be available on Maven Central. Stay tuned for the release announcement.

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

// Once you have authorized the user, use the pair of tokens to instantiate the module you need

// Create an instance of Meetings SDK
val meetingsSDK = zoom.meetings(userTokens)

// Create an instance of Users SDK
val usersSDK = zoom.users(userTokens)
```

## Non-Blocking vs Blocking Calls
The SDK provides both non-blocking and blocking API calls. You can choose the style that fits your use case.

### Non-Blocking
All API calls are suspendable functions, which means they must be called from a coroutine.

```kotlin
import com.kss.zoom.utils.call

// This is a non-blocking call via a coroutine.
val scheduledMeetings = call { meetingsSDK.listScheduled("your-zoom-user-id") }
```
If it suits you better, you can use the `callAsync` function to get a `CompletableFuture`.

```kotlin
import com.kss.zoom.utils.future

// This is a non-blocking call, it returns a CompletableFuture.
// It can be invoked from a coroutine or from a non-coroutine context.
val scheduledMeetings = callAsync { meetingsSDK.listScheduled() }

// It is a good fit for Java interop. Provide your own executor if needed.
val executor = Executors.newFixedThreadPool(4)
val scheduledMeetings = callAsync(executor) { meetingsSDK.listScheduled("your-zoom-user-id") }
```

### Blocking
If you prefer to use blocking calls, you can use the `callSync` function.

```kotlin
import com.kss.zoom.utils.callSync

// This call blocks the current thread until the result is available.
// It is designed to be called from a non-coroutine context.
val scheduledMeetings = callSync { meetingsSDK.listScheduled("your-zoom-user-id") }

// It is a good fit for Java interop. Provide your own executor if needed.
val executor = Executors.newFixedThreadPool(4)
val scheduledMeetings = callSync(executor) { meetingsSDK.listScheduled() }
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
val scheduledMeetings = call { meetingsSDK.listScheduled() }

// Handle the happy path
```

## Tracking Requests
The SDK allows you to track requests and responses for debugging and monitoring purposes.

```kotlin
val scheduledMeetings = call {
    meetingsSDK.withCorrelationId("your-correlation-id") {
        listScheduled("your-zoom-user-id")
    }
}
```

## Building from Source Code
To build the SDK from source code, clone the repository and run the following command:

```shell
./gradlew build
```

## Contributing
To contribute to the SDK, please read the [CONTRIBUTING.md](CONTRIBUTING.md) file.
