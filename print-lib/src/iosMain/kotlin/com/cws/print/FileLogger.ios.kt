package com.cws.print

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.writeToFile

actual class FileLogger(
    private var filepath: String
) : Logger {

    actual override fun open() {
        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSDocumentDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        )
        val internalDocumentsDir = paths.firstOrNull() as? String ?: return
        this.filepath = "$internalDocumentsDir/$filepath"
    }

    actual override fun close() {
        filepath = ""
    }

    @OptIn(ExperimentalForeignApi::class)
    actual override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        if (filepath.isNotEmpty()) {
            (formatLog(logLevel, tag, message, exception) as NSString).writeToFile(
                path = filepath,
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = null
            )
        }
    }

}