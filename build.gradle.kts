import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.9.10" apply false
    id("io.ktor.plugin") version "2.3.5" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10" apply false
    id("org.openapi.generator") version "6.6.0" apply false
}

group = "com.kotlinserversquad"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
    }
    apply(plugin = "org.jetbrains.kotlin.jvm")

    configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    tasks.withType<Test> {
        useJUnitPlatform()
        reports {
            junitXml.isOutputPerTestCase = true
        }
        this.testLogging {
            this.showStandardStreams = true
        }
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }
}

