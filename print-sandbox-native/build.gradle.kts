plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    macosArm64 {
        binaries {
            executable {
                entryPoint = "com.cws.print.sandbox.main"
            }
        }
    }

    linuxX64 {
        binaries {
            executable {
                entryPoint = "com.cws.print.sandbox.main"
            }
        }
    }

    mingwX64 {
        binaries {
            executable {
                entryPoint = "com.cws.print.sandbox.main"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":print-lib"))
                // Standard
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        val mingwX64Main by getting { dependsOn(nativeMain) }
        val linuxX64Main by getting { dependsOn(nativeMain) }
        val macosArm64Main by getting { dependsOn(nativeMain) }
    }
}