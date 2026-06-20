package com.cws.print

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.refTo
import kotlinx.cinterop.staticCFunction
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
object NativeExceptionHandler {

    private var file = -1
    private val sigunknownMessage = "Native crash with unknown signal!".encodeToByteArray()
    private val sigsegvMessage = "Native crash with SIGSEGV!".encodeToByteArray()
    private val sigabrtMessage = "Native crash with SIGABRT!".encodeToByteArray()
    private val sigfpeMessage = "Native crash with SIGFPE!".encodeToByteArray()
    private val sigillMessage = "Native crash with SIGILL!".encodeToByteArray()

    fun install(filepath: String) {
        file = open(filepath, O_WRONLY or O_CREAT or O_APPEND, 0x1b6)
        if (file == -1) {
            writeBytes(STDERR_FILENO, "Failed to open to write to crash file $filepath!".encodeToByteArray())
        }

        signal(SIGSEGV, staticCFunction(::handleSignal))
        signal(SIGABRT, staticCFunction(::handleSignal))
        signal(SIGFPE, staticCFunction(::handleSignal))
        signal(SIGILL, staticCFunction(::handleSignal))
    }

    private fun handleSignal(signal: Int) {
        val message = when (signal) {
            SIGSEGV -> sigsegvMessage
            SIGABRT -> sigabrtMessage
            SIGFPE -> sigfpeMessage
            SIGILL -> sigillMessage
            else -> sigunknownMessage
        }
        writeCrash(message)
        _exit(128 + signal)
    }

    private fun writeCrash(message: ByteArray) {
        writeBytes(STDERR_FILENO, message)
        if (file != -1) {
            writeBytes(file, message)
            close(file)
        }
    }

    @OptIn(UnsafeNumber::class)
    private fun writeBytes(fd: Int, bytes: ByteArray) {
        write(fd, bytes.refTo(0), bytes.size.toULong())
    }

}