pluginManagement {
    includeBuild("convention-plugins")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "zoomsdk"
include(":cli")
include(":sdk")
include(":examples:webhooks:ktor")
include(":examples:webhooks:nodejs")
include(":examples:webhooks:spring-boot")
