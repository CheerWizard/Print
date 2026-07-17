plugins {
    id("com.android.application")
}

android {
    namespace = "com.cws.print.sandbox.app"
    compileSdk = 37

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

dependencies {
    implementation(project(":print-sandbox"))
}