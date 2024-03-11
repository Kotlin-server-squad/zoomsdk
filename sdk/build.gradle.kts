plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get(), configurations.testImplementation.get())
}

configurations["integrationTestRuntimeOnly"]
    .extendsFrom(configurations.runtimeOnly.get(), configurations.testRuntimeOnly.get())

val junit_jupiter_version: String by project
val kotlin_version: String by project
val ktor_version: String by project

dependencies {
    // Project
    implementation(project(":common"))

    // A simple embedded server to capture OAuth2 redirect
    implementation("io.ktor:ktor-server-core:2.3.8")
    implementation("io.ktor:ktor-server-netty:2.3.8")

    // Webhook verification
    implementation("commons-codec:commons-codec:1.15")

    // Tests
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")

    "integrationTestImplementation"(kotlin("test", kotlin_version))
    "integrationTestImplementation"(kotlin("test-junit5", kotlin_version))
    "integrationTestImplementation"("org.junit.jupiter:junit-jupiter:$junit_jupiter_version")
    "integrationTestRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}

val integrationTest = task<Test>("integrationTest") {
    useJUnitPlatform()

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")
}