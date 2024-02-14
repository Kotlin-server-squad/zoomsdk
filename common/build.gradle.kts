plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

sourceSets {
    create("testCommon") {
        kotlin.srcDir("src/test-common/kotlin")
        resources.srcDir("src/test-common/resources")
    }
}

tasks.register<Jar>("testJar") {
    archiveClassifier.set("tests")
    from(sourceSets["testCommon"].output)
}

configurations {
    create("testCommonRuntime") {
        isCanBeResolved = true
        isCanBeConsumed = true
    }
}

artifacts {
    add("testCommonRuntime", tasks["testJar"])
}

val kotlin_version: String by project

dependencies {
    val testCommonImplementation by configurations.getting {
        extendsFrom(configurations["testImplementation"])
    }
    "testCommonImplementation"(project(":common"))
    "testCommonImplementation"(kotlin("test", kotlin_version))
    "testCommonImplementation"(kotlin("test-junit5", kotlin_version))
}
