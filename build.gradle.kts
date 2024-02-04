import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("io.ktor.plugin") version "2.3.8" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
    id("org.openapi.generator") version "6.6.0" apply false
kotlin("jvm") version "1.9.22"
}

group = "com.kotlinserversquad"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
    }
}

val javaVersion = 17

val junit_jupiter_version: String by project
val kotlin_version: String by project
val ktor_version: String by project


subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {

        // Core dependencies
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")

        // Ktor
        "implementation"("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
        "implementation"("io.ktor:ktor-client-content-negotiation:$ktor_version")

        // Tests
        "testImplementation"("io.ktor:ktor-client-mock:$ktor_version")
        "testImplementation"(kotlin("test", kotlin_version))
        "testImplementation"(kotlin("test-junit5", kotlin_version))
        "testImplementation"("org.junit.jupiter:junit-jupiter:$junit_jupiter_version")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "$javaVersion"
        }
    }
}

dependencies {
implementation(kotlin("stdlib-jdk8"))}
repositories {
mavenCentral()}
kotlin {
jvmToolchain(8)}