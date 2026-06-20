plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    id("com.android.application")
}

kotlin {
    androidTarget()

    jvm("desktop")

    js(IR) {
        browser {
            binaries.executable()
        }
        nodejs {
            binaries.executable()
        }
    }

    iosArm64 {
        binaries {
            framework {
                baseName = "PrintSandbox"
            }
        }
    }

    iosX64 {
        binaries {
            framework {
                baseName = "PrintSandbox"
            }
        }
    }

    iosSimulatorArm64 {
        binaries {
            framework {
                baseName = "PrintSandbox"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":print-lib"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(kotlin("stdlib-common"))
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
        }

        val iosMain by creating {
            dependsOn(commonMain)
        }

        val desktopMain by getting {
            dependsOn(commonMain)
        }

        val jsMain by getting {
            dependsOn(commonMain)
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