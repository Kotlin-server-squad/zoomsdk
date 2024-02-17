plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
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

    // Tests
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