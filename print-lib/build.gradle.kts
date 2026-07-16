@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.nmcp)
    `maven-publish`
    signing
}

group = "io.github.cheerwizard"
version = "1.0.1"

android {
    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags += listOf("-std=c++17")
            }
        }
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/cpp/native_exception_handler/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    compileSdk = 36
    namespace = "com.cws.print"

    defaultConfig {
        minSdk = 26
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }

    jvm("jvm")

    js {
        browser()
    }

    wasmJs {
        browser()
    }

    mingwX64()
    linuxX64()
    macosArm64()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.kotlinx.coroutines.core)
            }
            kotlin.srcDir("build/generated/src/commonMain/kotlin")
        }

        val jniMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(jniMain)
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }

        val jvmMain by getting {
            dependsOn(jniMain)
            dependencies {
                implementation(libs.ktor.client.java)
            }
        }

        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        val wasmJsMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.kotlinx.browser)
                implementation(libs.ktor.client.js)
            }
        }

        val posixMain by creating {
            dependsOn(commonMain)
        }

        val nativeMain by creating {
            dependsOn(posixMain)
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }

        val mingwX64Main by getting { dependsOn(nativeMain) }
        val linuxX64Main by getting { dependsOn(nativeMain) }
        val macosArm64Main by getting { dependsOn(nativeMain) }

        val iosMain by creating {
            dependsOn(posixMain)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }
}

// Publishing
publishing {
    publications.withType<MavenPublication> {
        val pubName = name
        val javadocJar = tasks.register("${pubName}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(pubName)
        }
        artifact(javadocJar)
        pom {
            name.set("Print")
            description.set("A simple cross platform logging library for KMP projects")
            url.set("https://github.com/CheerWizard/Print")

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            developers {
                developer {
                    id.set("cheerwizard")
                    name.set("Cheer Wizard")
                    email.set("mechanik2442@gmail.com")
                }
            }

            scm {
                connection.set("scm:git:github.com/CheerWizard/Print.git")
                developerConnection.set("scm:git:ssh://github.com/CheerWizard/Print.git")
                url.set("https://github.com/CheerWizard/Print")
            }
        }
    }
}

// Signing
signing {
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassword = System.getenv("GPG_SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

// Auto publish to Maven Central portal
nmcp {
    publishAllPublicationsToCentralPortal {
        username = System.getenv("SONATYPE_USERNAME")
        password = System.getenv("SONATYPE_PASSWORD")
        publishingType = "AUTOMATIC"
    }
}

data class JniTarget(
    val name: String,
    val generator: String,
    val jniInclude: String,
    val libraryName: (String) -> String,
)

val jniTargets = listOf(
    JniTarget(
        name = "linuxX64",
        generator = "Unix Makefiles",
        jniInclude = "linux",
        libraryName = { "lib$it.so" }
    ),
    JniTarget(
        name = "macosArm64",
        generator = "Unix Makefiles",
        jniInclude = "darwin",
        libraryName = { "lib$it.dylib" }
    ),
    JniTarget(
        name = "mingwX64",
        generator = "Visual Studio 17 2022",
        jniInclude = "win32",
        libraryName = { "$it.dll" }
    )
)

fun registerCmakeTask(
    projectName: String,
    target: JniTarget,
): TaskProvider<Task> {

    val javaHome = System.getenv("JAVA_HOME")
        ?: throw GradleException("JAVA_HOME is not defined")

    val outDir = layout.buildDirectory.dir("jni/${target.name}")

    val configure = tasks.register<Exec>("configureJni${target.name.replaceFirstChar(Char::uppercase)}") {
        group = "jni"

        doFirst {
            outDir.get().asFile.mkdirs()
        }

        workingDir(outDir.get().asFile)

        environment("JAVA_HOME", javaHome)

        commandLine(
            "cmake",
            "-G", target.generator,
            "-DCMAKE_BUILD_TYPE=Release",
            "-DJAVA_HOME=$javaHome",
            "-DCMAKE_INCLUDE_PATH=$javaHome/include;$javaHome/include/${target.jniInclude}",
            layout.projectDirectory.dir("src/cpp/$projectName").asFile.absolutePath
        )
    }

    val build = tasks.register<Exec>("buildNativeJni${target.name.replaceFirstChar(Char::uppercase)}") {
        group = "jni"

        dependsOn(configure)

        workingDir(outDir.get().asFile)

        environment("JAVA_HOME", javaHome)

        commandLine(
            "cmake",
            "--build",
            "."
        )
    }

    val copy = tasks.register<Copy>("copyJni${target.name.replaceFirstChar(Char::uppercase)}") {
        group = "jni"

        dependsOn(build)

        from(outDir.map { it.file(target.libraryName(projectName)) })

        into(layout.projectDirectory.dir("src/desktopMain/resources/jni/${target.name}"))
    }

    return tasks.register("buildJni${target.name.replaceFirstChar(Char::uppercase)}") {
        group = "jni"
        dependsOn(copy)
    }
}

val registeredTasks = jniTargets.associateWith {
    registerCmakeTask(
        projectName = "native_exception_handler",
        target = it
    )
}

val currentHost: String get() = when {
    org.gradle.internal.os.OperatingSystem.current().isLinux -> "linuxX64"
    org.gradle.internal.os.OperatingSystem.current().isMacOsX -> "macosArm64"
    org.gradle.internal.os.OperatingSystem.current().isWindows -> "mingwX64"
    else -> throw GradleException("Unsupported desktop OS")
}

tasks.register("buildJni") {
    group = "jni"
    dependsOn(registeredTasks.entries.first { it.key.name == currentHost }.value)
}
