import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

plugins {
    application
    alias(libs.plugins.kotlinMultiplatform)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val program = "zoomcli"
val junitVersion: String by project
val ktorVersion: String by project

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.kss.zoom.cli.ZoomCli")
}

val nativeTarget = when (val hostOs = System.getProperty("os.name")) {
    "Mac OS X" -> "MacosX64"
    "Linux" -> "LinuxX64"
    else -> throw GradleException("Host $hostOs is not supported in Kotlin/Native.")
}

fun KotlinNativeTargetWithHostTests.configureTarget() =
    binaries { executable { entryPoint = "main" } }

kotlin {
    macosX64 { configureTarget() }

    // Uncomment the following block to add support for Linux
    // Currently blocked by an issue with curl: https://youtrack.jetbrains.com/issue/KTOR-6361
//    linuxX64 { configureTarget() }

    val jvmTarget = jvm()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(project(":sdk"))
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("com.github.ajalt.clikt:clikt:4.3.0")
                implementation("com.github.ajalt.mordant:mordant:2.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-cio:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
        val jvmTest by getting {
            dependencies {

            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-curl:$ktorVersion")
            }
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
        val posixMain by creating {
            dependsOn(nativeMain)
        }
        val posixTest by creating {
            dependsOn(nativeTest)
        }
        arrayOf("macosX64" /*,"linuxX64"*/).forEach { targetName ->
            getByName("${targetName}Main").dependsOn(posixMain)
            getByName("${targetName}Test").dependsOn(posixTest)
        }
        arrayOf("macosX64"/*,"linuxX64"*/).forEach { targetName ->
            getByName("${targetName}Main").dependsOn(nativeMain)
            getByName("${targetName}Test").dependsOn(nativeTest)
        }
        tasks.withType<JavaExec> {
            // code to make run task in kotlin multiplatform work
            val compilation = jvmTarget.compilations.getByName<KotlinJvmCompilation>("main")

            val classes = files(
                compilation.runtimeDependencyFiles,
                compilation.output.allOutputs
            )
            classpath(classes)
        }
    }
    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
        archiveVersion.set("")

        from(jvmTarget.compilations.getByName("main").output)
        configurations = mutableListOf(
            jvmTarget.compilations.getByName("main").compileDependencyFiles,
            jvmTarget.compilations.getByName("main").runtimeDependencyFiles
        )
    }
    tasks.register("allRun") {
        group = "run"
        description = "Run $program on the JVM, on Node and natively"
        dependsOn("run", "jsNodeRun", "runDebugExecutable$nativeTarget")
    }
}

interface Injected {
    @get:Inject
    val exec: ExecOperations

    @get:Inject
    val fs: FileSystemOperations
}

abstract class InstallTask : DefaultTask() {
    @get:Input
    abstract val userHome: Property<String>

    @get:Input
    abstract val programName: Property<String>

    @get:Input
    abstract val sourceBinaryPath: Property<String>

    @get:Input
    abstract val targetBinaryPath: Property<String>

    @TaskAction
    fun performTask() {
        val programDir = Paths.get(userHome.get(), ".${programName.get()}")

        // Check if the directory exists. If not, create it.
        if (Files.notExists(programDir)) {
            Files.createDirectories(programDir)
            println("Created directory at $programDir")
        }
        val destinationBinaryPath = programDir.resolve(programName.get()).toString()

        // Copy the binary
        println("Copying ${sourceBinaryPath.get()} to $destinationBinaryPath")
        Files.copy(Paths.get(sourceBinaryPath.get()), File(destinationBinaryPath).toPath(), REPLACE_EXISTING)

        // Finish by checking if the binary is in the PATH
        val isPresentInPath = System.getenv("PATH")?.contains(programDir.toString()) ?: false
        if (isPresentInPath) {
            println("Congrats! You've successfully installed Zoom CLI. Run '${programName.get()}' to get started.")
        } else {
            val exportPath = "export PATH=$programDir:\$PATH"
            println(
                """
                        |Installation is almost complete. To finish, add ~/.zoomcli to your PATH environment variable.
                        |
                        |For Bash, add the following line to your ~/.bashrc or ~/.bash_profile:
                        |$exportPath
                        |
                        |For Zsh, add the following line to your ~/.zshrc:
                        |$exportPath
                        |
                        |Then, reload your shell configuration or restart your terminal.
                    """.trimMargin()
            )
        }
    }
}

tasks.register<InstallTask>("install") {
    programName.set(program)
    userHome.set(System.getProperty("user.home"))
    sourceBinaryPath.set(
        Paths.get(
            projectDir.path, "build", "bin",
            nativeTarget.first().lowercaseChar() + nativeTarget.substring(1),
            "debugExecutable", "cli.kexe"
        ).toAbsolutePath().toString()
    )
    targetBinaryPath.set(Paths.get(userHome.get(), ".zoomcli", "zoomcli").toString())
}
