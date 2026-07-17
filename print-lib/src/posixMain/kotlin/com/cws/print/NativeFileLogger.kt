package com.cws.print

import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fflush
import platform.posix.fopen
import platform.posix.fwrite
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalForeignApi::class)
class NativeFileLogger(
    private val filepath: String,
    private var flushPeriod: Duration = 3.seconds,
) : Logger {

    companion object {
        private const val TAG = "NativeFileLogger"
    }

    private var file: CPointer<FILE>? = null

    private val flushScope = CoroutineScope(Dispatchers.IO)
    private var flushJob: Job? = null
    private val flushLock = ReentrantLock()

    private val consoleLogger = ConsoleLogger()

    override fun open() {
        if (file != null) return

        val dir = filepath.substringBeforeLast("/", missingDelimiterValue = ".")
        makeDirs(dir)
        file = fopen(filepath, "a")

        consoleLogger.open()

        if (file == null) {
            consoleLogger.log(LogLevel.ERROR, TAG, "fopen FAILED for $filepath, errno=${platform.posix.errno}")
        }

        flushJob?.cancel()
        flushJob = flushScope.launch {
            while (isActive) {
                delay(flushPeriod)
                flushFile()
            }
        }
    }

    override fun close() {
        flushJob?.cancel()
        file?.let { file ->
            flushLock.withLock {
                fflush(file)
                fclose(file)
            }
        }
        file = null
    }

    override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        if (file != null) {
            val log = formatLog(logLevel, tag, message, exception)
            val bytes = log.encodeToByteArray()
            bytes.usePinned { pinned ->
                flushLock.withLock {
                    fwrite(pinned.addressOf(0), 1u, bytes.size.toULong(), file)
                }
            }
        }
        // flush immediately to file a FATAL level logs
        // this will also flush all logs before FATAL, which is a correct flow,
        // history of logs will give a better diagnostics for user
        if (logLevel == LogLevel.FATAL) {
            flushFile()
        }
    }

    private fun flushFile() {
        file?.let { file ->
            flushLock.withLock {
                fflush(file)
            }
        }
    }

}