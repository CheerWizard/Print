package com.cws.print

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.posix.*

@OptIn(UnsafeNumber::class, ExperimentalForeignApi::class)
actual fun CrashFileWriter.writeBytes(fd: Int, bytes: ByteArray) {
    write(fd, bytes.refTo(0), bytes.size.toULong())
}