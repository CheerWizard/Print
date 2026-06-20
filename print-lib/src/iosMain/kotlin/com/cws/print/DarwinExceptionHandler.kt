@file:OptIn(ExperimentalForeignApi::class)

package com.cws.print

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.staticCFunction
import platform.posix.SIGABRT
import platform.posix.SIGFPE
import platform.posix.SIGILL
import platform.posix.SIGSEGV
import platform.posix._exit
import platform.posix.SA_RESETHAND
import platform.posix.SA_RESTART
import platform.posix.sigaction

private val sigunknownMessage = "Native crash with unknown signal!".encodeToByteArray()
private val sigsegvMessage = "Native crash with SIGSEGV!".encodeToByteArray()
private val sigabrtMessage = "Native crash with SIGABRT!".encodeToByteArray()
private val sigfpeMessage = "Native crash with SIGFPE!".encodeToByteArray()
private val sigillMessage = "Native crash with SIGILL!".encodeToByteArray()

private val reportWriter = DarwinCrashWriter()

fun installDarwinExceptionHandler(filepath: String) {
    reportWriter.install(filepath)
    val sigaction = nativeHeap.alloc<sigaction>()
    sigaction.sa_flags = SA_RESETHAND or SA_RESTART
    sigaction.__sigaction_u.__sa_handler = staticCFunction(::handleSignal)
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