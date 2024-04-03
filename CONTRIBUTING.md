# Contributing to the Zoom SDK
Zoom SDK is a toolkit that helps developers integrate Zoom into their applications.
This document is an introduction into the project  and provides guidance on how to get involved in the SDK development.

## Project overview
The project was initially aimed at backend developers who wanted to integrate Zoom into their server applications.
The project has since evolved to include frontend developers who want to integrate Zoom into their web applications.
The web part of the project hasn't been started yet, but it's on the roadmap.

## How to get started
The project is written in Kotlin and uses Gradle as a build tool.
It requires Java 17 to build and run the project.

To get started, clone the repository and open it in IntelliJ IDEA or any
other IDE of your choice that supports Kotlin and Gradle.

The project leverages [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html), which allows us 
to write code that runs on both the JVM and JavaScript (and potentially other platforms in the future).

The project is divided into two main parts:
* `sdk`: The core of the project that contains the Zoom SDK.
* `examples`: Example applications that demonstrates how to use the Zoom SDK.

## SDK Design and Architecture
The SDK is designed to be modular and extensible. It is divided into several packages:
* `auth`: Implementation that handle authentication with Zoom.
* `client`: Implementation that handle communication with Zoom API.
* `sdk`: The core of the SDK that provides the main functionality.

In the root package, there is a single class called `Zoom` that provides the main entry point to the SDK.

Please note, throughout this document I use the term `module` and `SDK part` interchangeably.
This is not to be confused with Gradle modules, which are used to organize the project.

The project is designed for collaborative development. Each part of the SDK
is contained in its own package, which makes it easy to work on a specific part
of the SDK without affecting other parts.

Let's take a look at the `auth` package as an example:
* `IAuthorization.kt`: Interface that defines the contract for authorization.
* `Authorization.kt`: Implementation of the authorization.
* `config`: Configuration for the authorization. This is where you would put your Zoom `client ID` and `client secret`.
* `model`: Data classes that represent the authorization data. The `api` subpackage contains data classes that represent the requests to and responses from the Zoom API.

All other SDK modules follow the same pattern.

Shared code is placed in the `common` package. This includes model classes, exceptions, and other shared code.

## Examples
The `examples` Gradle module contains example applications that demonstrate how to use the Zoom SDK.

This is a good starting point for anyone who wants to get familiar with using the SDK.

In my recent presentation, I've shown how to use the SDK to create a simple command-line application
that lists scheduled meetings in a Zoom account. I'll add soon add it in the `examples` module.

## Testing
The project uses JUnit 5 for testing. The tests are located in the `test` source set.
I've been working on a switch to Kotlin multiplatform, which will allow us to write tests that run
on both the JVM and JavaScript. Once that's done JUnit 5 will be replaced with KotlinTest in the `commonTest` source set,
but it'll remain in the `jvmTest` source set.

I anticipate a need for end-to-end tests that run against the Zoom API. That's what the `integrationTest` source set is for.
When working with integration tests, please make sure to use a test Zoom account to avoid affecting production data.
As a contributor, you will be provided with a test Zoom account. Please [reach out to me](https://www.linkedin.com/in/zezulatomas/) if you need one.

## CI/CD
The project uses GitHub Actions for CI/CD. The CI/CD pipeline is defined in the `.github/workflows` directory.

The pipeline is divided into two jobs:
* `build`: This job builds the project and runs the unit tests. This happens on every push to the repository.
* `integration-test`: This job runs the integration tests. This currently only happens manually. Ideally it should trigger on every push to the `main` branch.

## Contributing
Contributions are welcome! Here are a few ways you can contribute:
* Report bugs and request features by opening an issue.
* Contribute code by opening a pull request.
* Review pull requests and provide feedback.
* Help with documentation by opening a pull request.
