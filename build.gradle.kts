plugins {
    id("root.publication")
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    id("org.jetbrains.kotlin.plugin.serialization").version("2.0.10").apply(false)
    kotlin("jvm") version "2.0.10"
    id("dev.mokkery").version("2.2.0").apply(false)
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(8)
}

subprojects {
    if (name == "integrationTest") {
        tasks.withType<Test> {
            if (gradle.startParameter.taskNames.any { it.contains("build") }) {
                // Disable tests for integrationTest by default.
                // Integration tests are expensive and should be run manually by calling `./gradlew integrationTest:test`.
                enabled = false
            }
        }
    }
}
