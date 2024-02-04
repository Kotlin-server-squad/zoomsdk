plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    // Project
    implementation(project(":authorization"))
    implementation(project(":meetings"))
}
