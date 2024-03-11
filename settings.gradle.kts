plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "zoom-sdk"

include(":common")
include(":examples")
include(":sdk")
