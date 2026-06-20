plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.nmcp)
    `maven-publish`
    signing
}

group = "io.github.cheerwizard"
version = "1.0.0"

android {
    defaultConfig {
//        externalNativeBuild {
//            cmake {
//                cppFlags += listOf("-std=c++17")
//            }
//        }

//        ndk {
//            abiFilters += listOf("arm64-v8a", "x86_64")
//        }
    }

//    externalNativeBuild {
//        cmake {
//            path = file("src/cpp/native_exception_handler/CMakeLists.txt")
//            version = "3.22.1"
//        }
//    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    jvm("jvm")
    js(IR) {
        browser {
            binaries.library()
        }
        nodejs {
            binaries.library()
        }
    }
    mingwX64()
    linuxX64()
    macosX64()
    macosArm64()
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content)
                implementation(libs.ktor.serialization.kotlinx.json)
                // Standard
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.kotlinx.coroutines.core)
                implementation(kotlin("stdlib-common"))
            }
            kotlin.srcDir("build/generated/src/commonMain/kotlin")
        }

        val jniMain by creating {
            dependencies {}
            dependsOn(commonMain)
        }

        val posixMain by creating {
            dependencies {}
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
            dependsOn(jniMain)
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.java)
            }
            dependsOn(jniMain)
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
            }
            dependsOn(commonMain)
        }

        val nativeMain by creating {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
            dependsOn(posixMain)
        }
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
        val macosX64Main by getting {
            dependsOn(nativeMain)
        }
        val macosArm64Main by getting {
            dependsOn(nativeMain)
        }

        val iosMain by creating {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
            dependsOn(posixMain)
        }
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}

android {
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

//val jniBuildDir = file("$buildDir/jni")
//
//fun cmakeTask(projectName: String, platform: String, generator: String) = tasks.register("buildJni_$platform") {
//    group = "jni"
//    doLast {
//        println("Running cmakeTask for platform:$platform project:$projectName")
//
//        val outDir = file("$jniBuildDir/$platform")
//        outDir.mkdirs()
//
//        val javaHome = System.getenv("JAVA_HOME") ?: "/usr/lib/jvm/java-21-openjdk-amd64"
//
//        val jniPlatformInclude = when (platform) {
//            "linux-x86_64" -> "linux"
//            "windows-x86_64" -> "win32"
//            "macos-x86_64" -> "darwin"
//            else -> throw GradleException("Unknown platform")
//        }
//
//        val jniIncludeArgs = listOf(
//            "-DCMAKE_BUILD_TYPE=Release",
//            "-DJAVA_HOME=$javaHome",
//            "-DCMAKE_INCLUDE_PATH=$javaHome/include;$javaHome/include/$jniPlatformInclude"
//        )
//
//        project.exec {
//            workingDir(outDir)
//            environment("JAVA_HOME", javaHome)
//            println("Running cmake -G $generator")
//            commandLine("cmake", "-G", generator, *jniIncludeArgs.toTypedArray(), "../../../src/cpp/$projectName")
//        }
//
//        project.exec {
//            workingDir(outDir)
//            environment("JAVA_HOME", javaHome)
//            println("Running cmake --build .")
//            commandLine("cmake", "--build", ".")
//        }
//
//        val libName = when (platform) {
//            "linux-x86_64" -> "lib$projectName.so"
//            "windows-x86_64" -> "$projectName.dll"
//            "macos-x86_64" -> "lib$projectName.dylib"
//            else -> throw GradleException("Unknown platform")
//        }
//
//        project.copy {
//            from("$outDir/$libName")
//            into("src/desktopMain/resources/jni/$platform")
//        }
//    }
//}
//
//// Determine platform once during configuration
//val osName = System.getProperty("os.name").lowercase()
//val osArch = System.getProperty("os.arch").lowercase()
//
//println("OS: $osName, Arch: $osArch")
//
//val generator = when {
//    osName.contains("windows") -> "Visual Studio 17 2022"
//    osName.contains("linux") -> "Unix Makefiles"
//    osName.contains("mac") -> "Unix Makefiles"
//    else -> throw GradleException("Unsupported OS: $osName")
//}
//
//val platform = when {
//    osName.contains("windows") && osArch.contains("64") -> "windows-x86_64"
//    osName.contains("linux") && osArch.contains("64") -> "linux-x86_64"
//    osName.contains("mac") && osArch.contains("64") -> "macos-x86_64"
//    else -> throw GradleException("Unsupported OS/Arch: $osName / $osArch")
//}
//
//val cmakeBuild = cmakeTask("native_exception_handler", platform, generator)
//
//tasks.register("buildJni") {
//    dependsOn(cmakeBuild)
//}