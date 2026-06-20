package com.cws.print

import java.io.File
import kotlin.use

actual open class PlatformNativeExceptionHandler actual constructor() {

    protected actual fun init() {
        val libName = "native_exception_handler"
        val os = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()

        val libFile = when {
            os.contains("linux") && arch.contains("64") -> {
                "jni/linux-x86_64/lib$libName.so"
            }
            os.contains("windows") && arch.contains("64") -> {
                "jni/windows-x86_64/$libName.dll"
            }
            os.contains("mac") -> {
                "jni/macos-x86_64/lib$libName.dylib"
            }
            else -> {
                System.loadLibrary(libName)
                return
            }
        }

        val tmpFile = createTempFile(suffix = File(libFile).extension)

        NativeExceptionHandler::class.java.getResourceAsStream("/$libFile")!!.use { input ->
            tmpFile.outputStream().use { it.write(input.readBytes()) }
        }

        System.load(tmpFile.absolutePath)
    }

}