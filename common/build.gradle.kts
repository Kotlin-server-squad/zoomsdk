plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

val kotlin_version: String by project
val ktor_version: String by project

dependencies {
    // Ktor client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
}
