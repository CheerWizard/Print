@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    id("com.android.application")
}

kotlin {
    androidTarget()

    jvm("desktop")

    js {
        browser {
            commonWebpackConfig { outputFileName = "print-sandbox.js" }
        }
        binaries.executable()
    }

    wasmJs {
        browser {
            commonWebpackConfig { outputFileName = "print-sandbox.js" }
        }
        binaries.executable()
    }

    macosArm64 {
        binaries {
            executable()
        }
    }

    linuxX64 {
        binaries {
            executable()
        }
    }

    mingwX64 {
        binaries {
            executable()
        }
    }

    iosArm64 {
        binaries {
            framework {
                baseName = "PrintSandbox"
                isStatic = true
            }
        }
    }

    iosSimulatorArm64 {
        binaries {
            framework {
                baseName = "PrintSandbox"
                isStatic = true
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Print
                implementation(project(":print-lib"))
                // Standard
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val composeMain by creating {
            dependencies {
                // Compose
                implementation(libs.runtime)
                implementation(libs.foundation)
                implementation(libs.ui.tooling.preview)
                implementation(libs.components.resources)
            }
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependencies {
                // Compose
                api("androidx.activity:activity-compose:1.10.1")
                api(libs.androidx.core.ktx)
            }
            dependsOn(composeMain)
        }

        val desktopMain by getting {
            dependencies {
                // Compose
                implementation(compose.desktop.currentOs)
            }
            dependsOn(composeMain)
        }

        val jsMain by getting {
            dependsOn(composeMain)
        }

        val wasmJsMain by getting {
            dependsOn(composeMain)
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        val mingwX64Main by getting { dependsOn(nativeMain) }
        val linuxX64Main by getting { dependsOn(nativeMain) }
        val macosArm64Main by getting { dependsOn(nativeMain) }

        val iosMain by creating {
            dependsOn(composeMain)
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
    namespace = "com.cws.print.sandbox"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cws.print.sandbox"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Force-disable the strict lock validation tasks
tasks.matching { it.name == "kotlinWasmStoreYarnLock" || it.name == "kotlinStoreYarnLock" }.configureEach {
    enabled = false
}