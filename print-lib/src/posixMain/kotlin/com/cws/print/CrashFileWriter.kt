@file:OptIn(ExperimentalForeignApi::class)

package com.cws.print

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import platform.posix.*

class CrashFileWriter {

    private var file = -1

    fun install(filepath: String) {
        file = open(filepath, O_WRONLY or O_CREAT or O_APPEND, 0x1b6)
        if (file == -1) {
            writeBytes(STDERR_FILENO, "Failed to open to write to crash report file $filepath!".encodeToByteArray())
        }
    }

    fun write(message: ByteArray) {
        writeBytes(STDERR_FILENO, message)
        if (file != -1) {
            writeBytes(file, message)
            close(file)
        }
    }

}

@OptIn(UnsafeNumber::class)
expect fun CrashFileWriter.writeBytes(fd: Int, bytes: ByteArray)