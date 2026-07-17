@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.nmcp)
    `maven-publish`
    signing
}

group = "io.github.cheerwizard"
version = "1.0.3"

kotlin {
    android {
        namespace = "com.cws.print"
        compileSdk = 37
        minSdk = 26
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

        val webMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(libs.kotlinx.browser)
                api(libs.ktor.client.js)
            }
        }

        val jsMain by getting {
            dependsOn(webMain)
        }

        val wasmJsMain by getting {
            dependsOn(webMain)
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

sealed class JniTarget {
    abstract val name: String
    abstract val libraryName: (String) -> String
    abstract val outputResourcePath: String

    abstract fun configureArgs(javaHome: String): List<String>
}

data class DesktopJniTarget(
    override val name: String,
    val generator: String,
    val jniInclude: String,
    override val libraryName: (String) -> String,
) : JniTarget() {
    override val outputResourcePath = "src/desktopMain/resources/jni/$name"

    override fun configureArgs(javaHome: String) = listOf(
        "-G", generator,
        "-DCMAKE_BUILD_TYPE=Release",
        "-DJAVA_HOME=$javaHome",
        "-DCMAKE_INCLUDE_PATH=$javaHome/include;$javaHome/include/$jniInclude",
    )
}

data class AndroidJniTarget(
    val abi: String,
    val minSdk: Int = 26,
    override val libraryName: (String) -> String = { "lib$it.so" },
) : JniTarget() {
    override val name = "android-$abi"
    override val outputResourcePath = "src/androidMain/jniLibs/$abi"

    override fun configureArgs(javaHome: String): List<String> {
        val ndkHome = resolveNdkHome()

        val toolchainFile = File(ndkHome, "build/cmake/android.toolchain.cmake")
        if (!toolchainFile.exists()) {
            throw GradleException("Android NDK toolchain file not found at $toolchainFile")
        }

        val ninjaPath = resolveNinjaPath()

        return listOf(
            "-G", "Ninja",
            "-DCMAKE_MAKE_PROGRAM=$ninjaPath",
            "-DCMAKE_TOOLCHAIN_FILE=${toolchainFile.absolutePath}",
            "-DANDROID_ABI=$abi",
            "-DANDROID_PLATFORM=android-$minSdk",
            "-DCMAKE_BUILD_TYPE=Release",
        )
    }
}

fun registerCmakeTask(
    projectName: String,
    target: JniTarget,
): TaskProvider<Task> {
    val javaHome = System.getenv("JAVA_HOME")

    val safeName = target.name.replaceFirstChar(Char::uppercase)
        .replace("-", "")
        .replace(Regex("[^A-Za-z0-9]"), "")

    val outDir = layout.buildDirectory.dir("jni/${target.name}")

    val configure = tasks.register<Exec>("configureJni$safeName") {
        group = "jni"
        doFirst { outDir.get().asFile.mkdirs() }
        workingDir(outDir.get().asFile)
        environment("JAVA_HOME", javaHome)
        commandLine(
            listOf("cmake") +
                    target.configureArgs(javaHome) +
                    listOf(layout.projectDirectory.dir("src/cpp/$projectName").asFile.absolutePath)
        )
    }

    val build = tasks.register<Exec>("buildNativeJni$safeName") {
        group = "jni"
        dependsOn(configure)
        workingDir(outDir.get().asFile)
        environment("JAVA_HOME", javaHome)
        commandLine("cmake", "--build", ".")
    }

    val copy = tasks.register<Copy>("copyJni$safeName") {
        group = "jni"
        dependsOn(build)
        from(outDir.map { it.file(target.libraryName(projectName)) })
        into(layout.projectDirectory.dir(target.outputResourcePath))
    }

    return tasks.register("buildJni$safeName") {
        group = "jni"
        dependsOn(copy)
    }
}

val desktopJniTargets = listOf(
    DesktopJniTarget(name = "linuxX64", generator = "Unix Makefiles", jniInclude = "linux", libraryName = { "lib$it.so" }),
    DesktopJniTarget(name = "macosArm64", generator = "Unix Makefiles", jniInclude = "darwin", libraryName = { "lib$it.dylib" }),
    DesktopJniTarget(name = "mingwX64", generator = "Visual Studio 17 2022", jniInclude = "win32", libraryName = { "$it.dll" }),
)

val androidJniTargets = listOf(
    AndroidJniTarget(abi = "arm64-v8a"),
    AndroidJniTarget(abi = "x86_64"),
)

val allJniTargets: List<JniTarget> = desktopJniTargets + androidJniTargets

val registeredTasks = allJniTargets.associateWith {
    registerCmakeTask(projectName = "native_exception_handler", target = it)
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

tasks.register("buildJniAndroid") {
    group = "jni"
    dependsOn(registeredTasks.filterKeys { it is AndroidJniTarget }.values)
}

fun Project.resolveNdkHome(): String {
    // Return explicitly set variable
    System.getenv("ANDROID_NDK_HOME")?.let { return it }

    // Fallback to Android SDK locally installed on machine
    val sdkDir = run {
        val localProps = Properties()
        val localPropsFile = rootProject.file("local.properties")
        if (localPropsFile.exists()) {
            localProps.load(localPropsFile.inputStream())
        }
        localProps.getProperty("sdk.dir") ?: System.getenv("ANDROID_HOME")
    } ?: throw GradleException("Could not resolve Android SDK location (no local.properties sdk.dir or ANDROID_HOME)")

    val ndkRoot = File(sdkDir, "ndk")
    val ndkVersion = ndkRoot.listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }
        ?: throw GradleException("No NDK version found under $ndkRoot. Install one via Android Studio's SDK Manager.")

    return ndkVersion.absolutePath
}

fun Project.resolveNinjaPath(): String {
    val sdkDir = resolveSdkDir()
    val cmakeRoot = File(sdkDir, "cmake")
    val cmakeVersionDir = cmakeRoot.listFiles()?.filter { it.isDirectory }?.maxByOrNull { it.name }
        ?: throw GradleException("No SDK-bundled CMake/Ninja found under $cmakeRoot. Install 'CMake' via SDK Manager.")
    val ninja = File(cmakeVersionDir, "bin/ninja")
    if (!ninja.exists()) throw GradleException("ninja binary not found at $ninja")
    return ninja.absolutePath
}

fun Project.resolveSdkDir(): String {
    // 1. local.properties (standard Android project convention)
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        val localProps = Properties()
        localProps.load(localPropsFile.inputStream())
        localProps.getProperty("sdk.dir")?.let { return it }
    }

    // 2. Environment variable fallback
    System.getenv("ANDROID_HOME")?.let { return it }
    System.getenv("ANDROID_SDK_ROOT")?.let { return it } // older/alternate env var some setups still use

    throw GradleException(
        "Could not resolve Android SDK location. Set 'sdk.dir' in local.properties, " +
                "or export ANDROID_HOME / ANDROID_SDK_ROOT."
    )
}
