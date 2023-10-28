val ktor_version: String by project
val coroutines_version: String by project

plugins {
    id("org.openapi.generator")
}

group = "zoomsdk.kotlinserversquad.com"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.1.5"))
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.1.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

fun createOpenApiTask(
    apiName: String,
    specFileName: String
): org.openapitools.generator.gradle.plugin.tasks.GenerateTask {
    return task(
        name = "openApiGenerator_$apiName",
        type = org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class
    ) {
        generatorName.set("kotlin-spring")
        inputSpec.set("$projectDir/spec/$specFileName")
        outputDir.set("$projectDir/")
        apiPackage.set("com.kotlinserversquad.zoomsdk.api.generated")
        packageName.set("com.kotlinserversquad.zoomsdk.api.generated")
        generateApiTests.set(false)
        generateModelTests.set(false)
        validateSpec.set(true)
        configOptions.set(
            mapOf(
                "dateLibrary" to "java8",
                "gradleBuildFile" to "false",
                "delegatePattern" to "true",
                "serializationLibrary" to "jackson",
                "useSwaggerUI" to "true",
                "interfaceOnly" to "true",
                "generateModelDocumentation" to "false",
                "documentationProvider" to "none",
                "useBeanValidation" to "true",
                "exceptionHandler" to "false",
                "reactive" to "true",
                "useSpringBoot3" to "true",
            )
        )
    }
}

arrayOf("meetings-v2.yaml").forEach { specFileName ->
    val apiName = specFileName.substringBefore("-")
    val task = createOpenApiTask(apiName, specFileName)
    tasks.getByName("compileKotlin").dependsOn(task)
}