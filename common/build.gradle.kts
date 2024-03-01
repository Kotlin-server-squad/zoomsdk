plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

val kotlin_version: String by project
val ktor_version: String by project
val kotlinx_coroutines_slf4j_version: String by project
val logback_version: String by project

dependencies {

    // Core dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinx_coroutines_slf4j_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Ktor client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
}
