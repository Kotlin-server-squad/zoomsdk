# Zoom SDK

## Overview
This is a Kotlin SDK for Zoom API. It is a wrapper around the [Zoom API](https://marketplace.zoom.us/docs/api-reference/zoom-api) and provides a convenient way to use the API.

## Pre-requisites
In order to use the SDK, you need to have the following:
1. Register an account with [Zoom](https://zoom.us/)
2. Create an app in the [Zoom App Marketplace](https://marketplace.zoom.us/)
3. Enable OAuth authentication in the app, see [here](https://developers.zoom.us/docs/zoom-apps/authentication)

## Usage
Using your app's client ID and client secret, you can create a Zoom SDK instance as follows:
```kotlin
import com.kss.zoomsdk.client.ZoomClient

val client = ZoomClient.create(
    clientId = "your-client-id",
    clientSecret = "your-client-secret"
)
```
Now you can use the client to make API calls.

### OAuth Authentication
Zoom API involves the end-user to authorize your app to access their data. This is done by redirecting the user to Zoom's authorization page. Once the user authorizes your app, Zoom will redirect the user back to your app with an authorization code. You can then exchange this code for an access token and refresh token. The access token is used to make API calls. The refresh token is used to refresh the access token when it expires.

To get the authorization URL, you can use the `getAuthorizationUrl` method of the `Auth` class. You need to provide the redirect URL as a parameter. The redirect URL must be one of the redirect URLs you have specified in your app's settings in the Zoom App Marketplace.

```kotlin
val authUrl = client.auth().getAuthorizationUrl("https://your-redirect-url")
```
Now, you can use `authUrl` to redirect the user to Zoom's authorization page. 
Once the user authorizes your app, Zoom will redirect the user back to your app with an authorization code.
You can then exchange this code for an access token and refresh token as follows:

```kotlin
val userAuthorization = client.auth().authorizeUser("your-authorization-code")
println("Access token: ${userAuthorization.accessToken}, Refresh token: ${userAuthorization.refreshToken}")
```
Once authorized, you can use the `userAuthorization.accessToken` to make API calls. For example, to get a list of meetings scheduled by your user, you can do the following:

```kotlin
val meetings = client.meetings().listScheduled(userAuthorization.accessToken)
```

### Refreshing Access Token
The access token expires after a certain period of time. You can use the refresh token to get a new access token as follows:

```kotlin
val refreshedUserAuthorization = client.auth().refreshUserAuthorization(userAuthorization.refreshToken)
println("New access token: ${refreshedUserAuthorization.accessToken}, New refresh token: ${refreshedUserAuthorization.refreshToken}")
```
