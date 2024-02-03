
plugins {
    id("org.openapi.generator")
}

tasks.register<Exec>("generateSwaggerCode") {
    group = "Application"
    description = "Runs a shell script to generate the code from the OpenAPI specification (json, yaml)."
    commandLine = listOf("./scripts/generate-swagger-code.sh")
}
tasks.named("build") {
    dependsOn("generateSwaggerCode")
}

sourceSets {
    main {
        kotlin {
            srcDir("build/generated/src/main/kotlin")
        }
    }
}