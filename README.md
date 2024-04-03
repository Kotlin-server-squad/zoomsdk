# Zoom SDK

## Overview
This is a Kotlin SDK for the [Zoom API](https://marketplace.zoom.us/docs/api-reference/introduction).
The SDK provides more than just a simple way to make API calls to Zoom.

## Benefits
* Simplifies integration and interaction with Zoom.
* Handles the OAuth authentication process and makes authenticated requests to the Zoom API.
* Highly flexible and customizable. Use as much or as little as you need.

## Supported Platforms
The SDK is built using Kotlin Multiplatform, which allows it to run on multiple platforms.

Currently, the SDK supports the following platforms:
* JVM (Kotlin/Java)
* JavaScript (Node.js, Browser)

## How to Use It?
See the platform-specific guides for detailed instructions on how to use the SDK.
* [JVM](doc/usage-jvm.md)
* [JavaScript](doc/usage-js.md)

## Installation
The SDK will be available on Maven Central. Stay tuned for the release announcement.

## Prerequisites
In order to use the SDK, you need to have the following:
1. Register an account with [Zoom](https://zoom.us/)
2. Create an app in the [Zoom App Marketplace](https://marketplace.zoom.us/)
3. Enable OAuth authentication in the app, see [here](https://developers.zoom.us/docs/zoom-apps/authentication)

## Building from Source Code
To build the SDK from source code, clone the repository and run the following command:

```shell
./gradlew build
```

## Contributing
To contribute to the SDK, please read the [CONTRIBUTING.md](CONTRIBUTING.md) file.
