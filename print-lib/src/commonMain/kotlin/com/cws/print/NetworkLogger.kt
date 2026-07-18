package com.cws.print

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

data class NetworkLogEntry(
    var timestamp: Long = 0,
    var level: LogLevel = LogLevel.NONE,
    var tag: String = "",
    var message: String = "",
    var exception: Throwable? = null
)

class NetworkLogBuffer {

    constructor(
        capacity: Int,
        logs: Array<NetworkLogEntry> = Array(capacity) { NetworkLogEntry() },
    ) {
        this.logs = logs
    }

    var logs: Array<NetworkLogEntry>
        private set

    var logIndex: Int = 0
        private set

    fun add(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        logs[logIndex++].apply {
            this.timestamp = getCurrentTimeMillis()
            this.level = logLevel
            this.tag = tag
            this.message = message
            this.exception = exception
        }
    }

    fun isEmpty(): Boolean = logIndex == 0

    fun isFull(): Boolean = logIndex > logs.lastIndex

    fun clear() {
        logIndex = 0
    }

}

abstract class NetworkLogger(
    maxLogsCount: Int = 16,
) : Logger {

    protected abstract val tag: String
    protected abstract val sendPeriod: Duration
    protected abstract val baseUrl: String

    private var httpClient: HttpClient? = null
    private val logs = NetworkLogBuffer(maxLogsCount)
    private val consoleLogger = ConsoleLogger()

    private val sendScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var sendJob: Job? = null
    private val sendMutex = Mutex()

    override fun open() {
        if (sendJob?.isActive == true) return
        httpClient = provideHttpClient()
        sendJob?.cancel()
        sendJob = sendScope.launch {
            while (isActive) {
                delay(sendPeriod)
                sendLogs()
            }
        }
    }

    override fun close() {
        sendJob?.cancel()
        sendJob = null
        httpClient?.let { client ->
            if (client.isActive) {
                client.close()
            }
        }
        httpClient = null
    }

    override fun log(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        if (logs.logIndex >= 0) {
            if (logs.isFull()) {
                sendScope.launch {
//                    sendLogs()
                    addLog(logLevel, tag, message, exception)
                }
            } else {
                addLog(logLevel, tag, message, exception)
                if (logLevel == LogLevel.FATAL) {
                    sendScope.launch {
//                        sendLogs()
                    }
                }
            }
        }
    }

    private fun addLog(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        logs.add(logLevel, tag, message, exception)
    }

    private suspend fun sendLogs() {
        if (logs.isEmpty()) return

        httpClient?.let { httpClient ->
            sendMutex.withLock {
                val requestBody = getRequestBody(logs.logs)

                logs.clear()

                val response = httpClient.post(baseUrl) {
                    url {
                        getQueryParams().forEach { (key, value) ->
                            parameters.append(key, value)
                        }
                    }
                    contentType(getContentType())
                    setBody(requestBody.toString())
                }

                if (response.status.value in 200..299) {
                    consoleLogger.log(LogLevel.INFO, tag, "Log successfully sent to $baseUrl")
                } else {
                    consoleLogger.log(LogLevel.ERROR, tag, "Failed to send log to $baseUrl")
                }
            }
        }
    }

    protected abstract fun getRequestBody(logs: Array<NetworkLogEntry>): Any
    protected abstract fun getQueryParams(): Map<String, String>
    protected abstract fun getContentType(): ContentType

}