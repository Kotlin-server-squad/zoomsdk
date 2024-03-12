plugins {
    kotlin("jvm")
}

val ktor_version: String by project

dependencies {
    implementation(project(":common"))
    implementation(project(":sdk"))
    implementation("io.ktor:ktor-client:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
}
