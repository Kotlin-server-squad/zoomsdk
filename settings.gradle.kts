plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "zoom-sdk"

include(":sdk")

// Examples
include(":examples:ktor-webhooks")
include(":examples:spring-boot-webhooks")
