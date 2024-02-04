import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.9.22" apply false
    id("io.ktor.plugin") version "2.3.8" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
    id("org.openapi.generator") version "6.6.0" apply false
}

group = "com.kotlinserversquad"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
    }
}

val javaVersion = 17

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")


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

