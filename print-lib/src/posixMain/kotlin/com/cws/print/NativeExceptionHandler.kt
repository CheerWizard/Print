@file:OptIn(ExperimentalForeignApi::class)

package com.cws.print

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import platform.posix.*

object NativeExceptionHandler {

    private val sigunknownMessage = "Native crash with unknown signal!".encodeToByteArray()
    private val sigsegvMessage = "Native crash with SIGSEGV!".encodeToByteArray()
    private val sigabrtMessage = "Native crash with SIGABRT!".encodeToByteArray()
    private val sigfpeMessage = "Native crash with SIGFPE!".encodeToByteArray()
    private val sigillMessage = "Native crash with SIGILL!".encodeToByteArray()

    private val reportWriter = NativeReportWriter()

    fun install(filepath: String) {
        reportWriter.install(filepath)
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
        reportWriter.write(message)
        _exit(128 + signal)
    }

}