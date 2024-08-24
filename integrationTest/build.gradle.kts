plugins {
    kotlin("jvm")
}

val ktor_version: String by extra

dependencies {
    implementation(project(":sdk"))
    testImplementation(libs.kotlin.test)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    // Date and time
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")

    //Ktor client
    testImplementation("io.ktor:ktor-client-core:$ktor_version")
    testImplementation("io.ktor:ktor-client-cio:$ktor_version")
}
