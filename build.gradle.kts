plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.nmcp) apply false
}

//subprojects {
//    configurations.configureEach {
//        resolutionStrategy.eachDependency {
//            if (requested.group == "org.jetbrains.kotlin") {
//                useVersion("2.2.10")
//            }
//        }
//    }
//}
