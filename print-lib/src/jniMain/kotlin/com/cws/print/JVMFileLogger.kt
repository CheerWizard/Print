package com.cws.print

import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.File
import kotlin.time.Duration

class JVMFileLogger(
    private val file: File,
    private val flushPeriod: Duration,
) : Logger {

    private var stream: BufferedOutputStream? = null

    private val flushScope = CoroutineScope(Dispatchers.IO)
    private var flushJob: Job? = null
    private val flushLock = ReentrantLock()

    override fun open() {
        if (stream != null) return

        file.parentFile?.mkdirs()
        if (!file.exists()) {
            file.createNewFile()
        }

        stream = BufferedOutputStream(file.outputStream())

        flushJob?.cancel()
        flushJob = flushScope.launch {
            while (isActive) {
                delay(flushPeriod)
            }
        }
    }

    override fun close() {
        stream?.let { stream ->
            flushLock.withLock {
                stream.flush()
                stream.close()
            }
        }
        stream = null
    }

    override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        val formatted = formatLog(logLevel, tag, message, exception).toByteArray()
        flushLock.withLock {
            stream?.write(formatted)
        }
        // flush immediately to file a FATAL level logs
        // this will also flush all logs before FATAL, which is a correct flow,
        // history of logs will give a better diagnostics for user
        if (logLevel == LogLevel.FATAL) {
            flushFile()
        }
    }

    private fun flushFile() {
        stream?.let { stream ->
            flushLock.withLock {
                stream.flush()
            }
        }
    }

}