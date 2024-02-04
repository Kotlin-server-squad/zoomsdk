plugins {
id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"}
rootProject.name = "zoomsdk"

include(":authorization")
include(":client")
include(":meetings")
include(":sdk")
