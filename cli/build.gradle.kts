plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    project(":sdk")

    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.github.ajalt.clikt:clikt:4.2.2")

    // A simple embedded server to capture OAuth2 redirect
    implementation("io.ktor:ktor-server-core:2.3.8")
    implementation("io.ktor:ktor-server-netty:2.3.8")

    // Serialization
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.2")

    // Logging
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-core:1.3.12")
    implementation("ch.qos.logback:logback-classic:1.3.12")
}
