val junit_jupiter_version: String by project
val kotlin_version: String by project
val ktor_version: String by project

plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    // Project
    implementation(project(":client"))

    // Core dependencies
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    // Ktor client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")

    // Tests
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
    testImplementation(kotlin("test", kotlin_version))
    testImplementation(kotlin("test-junit5", kotlin_version))
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_jupiter_version")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
