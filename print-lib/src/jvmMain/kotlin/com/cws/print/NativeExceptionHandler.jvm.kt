package com.cws.print

import java.io.File
import kotlin.io.path.absolutePathString
import kotlin.io.path.outputStream
import kotlin.use

actual open class PlatformNativeExceptionHandler actual constructor() {

    @Suppress("UnsafeDynamicallyLoadedCode")
    protected actual fun init() {
        val libName = "native_exception_handler"
        val os = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()

        val libFile = when {
            os.contains("win") && arch.contains("64") -> "jni/windows-x86_64/$libName.dll"
            os.contains("mac") -> "jni/macos-x86_64/lib$libName.dylib"
            os.contains("linux") && arch.contains("64") -> "jni/linux-x86_64/lib$libName.so"
            else -> throw UnsatisfiedLinkError("Unsupported desktop platform: os=$os arch=$arch")
        }

        val tmpFile = kotlin.io.path.createTempFile(suffix = File(libFile).extension)
        NativeExceptionHandler::class.java.getResourceAsStream("/$libFile")!!.use { input ->
            tmpFile.outputStream().use { it.write(input.readBytes()) }
        }
        System.load(tmpFile.absolutePathString())
    }

}