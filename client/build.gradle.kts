val ktor_version: String by project

plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    // Ktor client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")

}
