plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("module.publication")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.7" // Specify the desired JaCoCo version
    reportsDirectory = file("$buildDir/reports/jacoco")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    js(IR) {
        moduleName = "zoomsdk"
        browser {
            webpackTask {
                output.libraryTarget = "umd"
            }
        }
        nodejs {}
        binaries.executable()
    }

    sourceSets {
        val coroutinesVersion: String by project
        val ktorVersion: String by project
        val logbackVersion: String by project
        val slf4jVersion: String by project
        val junitVersion: String by project
        val kotlinLoggingVersion: String by project

        val commonMain by getting {
            dependencies {
                // Core Ktor client
                implementation("io.ktor:ktor-client-core:$ktorVersion")

                // JSON serialization support
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")

                // Logging
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.github.oshai:kotlin-logging:$kotlinLoggingVersion")

                // Date and time
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")

                // Webhook verification
                implementation("commons-codec:commons-codec:1.15")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$slf4jVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation("org.junit.jupiter:junit-jupiter:$junitVersion")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
            tasks.withType<Test> {
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                }
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                // Logging
                implementation("io.github.oshai:kotlin-logging-js:$kotlinLoggingVersion")

                // Add npm dependency for full ICU support
                implementation(npm("@js-joda/timezone", "2.18.2"))
            }
        }
        val jsTest by getting {
            dependencies {
                // JS specific test dependencies
            }
        }
    }
    tasks.register("jacocoTestReport", JacocoReport::class) {
        dependsOn("jvmTest")
        val coverageSourceDirs = arrayOf(
            "src/commonMain",
            "src/jvmMain"
        )

        val classFiles = File("${buildDir}/classes/kotlin/jvm/")
            .walkBottomUp()
            .toSet()

        classDirectories.setFrom(classFiles)
        sourceDirectories.setFrom(files(coverageSourceDirs))

        executionData
            .setFrom(files("${buildDir}/jacoco/jvmTest.exec"))

        reports {
            xml.required = true
            html.required = true
        }
    }
}
