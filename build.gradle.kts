plugins {
    id("root.publication")
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    id("org.jetbrains.kotlin.plugin.serialization").version("2.0.10").apply(false)
}
