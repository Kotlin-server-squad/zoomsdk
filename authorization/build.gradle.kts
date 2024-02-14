plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    // Project
    implementation(project(":client"))
    implementation(project(":common"))
    testImplementation(project(":common", "testCommonRuntime"))
}
