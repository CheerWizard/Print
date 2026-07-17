pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "print"

include(":print-lib")
include(":print-sandbox")
include(":print-sandbox-android")
include(":print-sandbox-native")