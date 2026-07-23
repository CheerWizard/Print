@file:OptIn(ExperimentalForeignApi::class)

package com.cws.print

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDomainMask
import platform.posix.S_IRWXU
import platform.posix.mkdir
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

actual fun makeDirs(path: String) {
    val parts = path.split("/").filter { it.isNotEmpty() }
    var current = if (path.startsWith("/")) "/" else ""
    for (part in parts) {
        current += "$part/"
        mkdir(current, S_IRWXU.toUShort()) // ignore result - EEXIST is fine
    }
}

actual class FileLogger(
    private val filepath: String,
    private val flushPeriod: Duration = 3.seconds
) : Logger {

    private var nativeFileLogger: NativeFileLogger? = null

    actual override fun open() {
        if (nativeFileLogger != null) return

        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSApplicationSupportDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        )

        val appSupportPath = paths.firstOrNull() as? String ?: return

        // making sure that Application Support directory structure actually exists
        NSFileManager.defaultManager.createDirectoryAtPath(
            appSupportPath,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )

        // exclude log directory from backups, since logs can take some space
        val url = NSURL.fileURLWithPath(appSupportPath)
        url.setResourceValue(
            value = true,
            forKey = NSURLIsExcludedFromBackupKey,
            error = null
        )

        nativeFileLogger = NativeFileLogger(
            "${appSupportPath}/${filepath}",
            flushPeriod
        ).apply {
            open()
        }
    }

    actual override fun close() {
        nativeFileLogger?.close()
        nativeFileLogger = null
    }

    actual override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        nativeFileLogger?.log(logLevel, tag, message, exception)
    }

}