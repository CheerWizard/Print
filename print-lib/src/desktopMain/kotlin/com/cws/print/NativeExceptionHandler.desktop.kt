package com.cws.print

import java.io.File
import kotlin.io.path.absolutePathString
import kotlin.io.path.outputStream
import kotlin.use

actual object JniLibrary {

    @Suppress("UnsafeDynamicallyLoadedCode")
    actual fun load(libraryName: String) {
        val os = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()

        val libFile = when {
            os.contains("win") && arch.contains("64") -> "jni/windows-x86_64/$libraryName.dll"
            os.contains("mac") -> "jni/macos-x86_64/lib$libraryName.dylib"
            os.contains("linux") && arch.contains("64") -> "jni/linux-x86_64/lib$libraryName.so"
            else -> throw UnsatisfiedLinkError("Unsupported desktop platform: os=$os arch=$arch")
        }

        val tmpFile = kotlin.io.path.createTempFile(suffix = File(libFile).extension)
        NativeExceptionHandler::class.java.getResourceAsStream("/$libFile")!!.use { input ->
            tmpFile.outputStream().use { it.write(input.readBytes()) }
        }
        System.load(tmpFile.absolutePathString())
    }

}