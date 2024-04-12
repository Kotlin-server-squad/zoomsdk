import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests

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
    linuxX64 { configureTarget() }

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
        arrayOf("macosX64", "linuxX64").forEach { targetName ->
            getByName("${targetName}Main").dependsOn(posixMain)
            getByName("${targetName}Test").dependsOn(posixTest)
        }
        arrayOf("macosX64", "linuxX64").forEach { targetName ->
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
    tasks.register<Copy>("install") {
        group = "run"
        description = "Build the native executable and install it"
        val destDir = "/usr/local/bin"

        dependsOn("runDebugExecutable$nativeTarget")
        println("Installing $program to $destDir")
        val targetLowercase = nativeTarget.first().lowercaseChar() + nativeTarget.substring(1)
        val folder = "cli/build/bin/$targetLowercase/debugExecutable"
        println("$ cp $folder/cli.kexe $destDir/$program")
        from(folder) {
            include("cli.kexe")
            rename { program }
        }
        into(destDir)
        doLast {
            println("$ cp $folder/cli.kexe $destDir/$program")
        }
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

tasks.register("completions") {
    group = "run"
    description = "Generate Bash/Zsh/Fish completion files"
    dependsOn(":install")
    val injected = project.objects.newInstance<Injected>()
    val shells = listOf(
        Triple("bash", file("completions/$program.bash"), "/usr/local/etc/bash_completion.d"),
        Triple("zsh", file("completions/_$program.zsh"), "/usr/local/share/zsh/site-functions"),
        Triple("fish", file("completions/$program.fish"), "/usr/local/share/fish/vendor_completions.d"),
    )
    for ((SHELL, FILE, INSTALL) in shells) {
        actions.add {
            println("Updating   $SHELL completion file at $FILE")
            injected.exec.exec {
                commandLine(program, "--generate-completion", SHELL)
                standardOutput = FILE.outputStream()
            }
            println("Installing $SHELL completion into $INSTALL")
            injected.fs.copy {
                from(FILE)
                into(INSTALL)
            }
        }
    }
    doLast {
        println("On macOS, follow the following instructions to configure shell completions")
        println("👀 https://docs.brew.sh/Shell-Completion 👀")
    }
}