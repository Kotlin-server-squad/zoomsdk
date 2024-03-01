plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

val kotlin_version: String by project
val ktor_version: String by project

dependencies {

    // Core dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.8.0")
    implementation("ch.qos.logback:logback-classic:1.2.6")

    // Ktor client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
}
