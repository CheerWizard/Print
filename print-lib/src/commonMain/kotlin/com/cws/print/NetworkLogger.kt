package com.cws.print

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration

abstract class NetworkLogger(
    var logLevel: LogLevel = LogLevel.WARNING
) : Logger {

    protected abstract val tag: String
    protected abstract val sendPeriod: Duration
    protected abstract val baseUrl: String

    private var httpClient: HttpClient? = null
    private var scope: CoroutineScope? = null
    private val logs = LogBuffer(100)
    private val consoleLogger = ConsoleLogger()
    private val lock = ReentrantLock()

    override fun open() {
        if (scope?.isActive == true) return
        httpClient = provideHttpClient()
        scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        scope?.launch {
            while (isActive) {
                delay(sendPeriod)
                sendLogs()
            }
        }
    }

    override fun close() {
        scope?.cancel()
        scope = null
        httpClient?.let { client ->
            if (client.isActive) {
                client.close()
            }
        }
        httpClient = null
    }

    override fun log(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        if (logLevel.ordinal >= this.logLevel.ordinal && logs.logIndex >= 0) {
            if (logs.isFull()) {
                scope?.launch {
                    sendLogs()
                    addLog(logLevel, tag, message, exception)
                }
            } else {
                addLog(logLevel, tag, message, exception)
            }
        }
    }

    private fun addLog(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        lock.withLock {
            logs.add(logLevel, tag, message, exception)
        }
    }

    private suspend fun sendLogs() {
        lock.withLock {
            val httpClient = this.httpClient
            if (httpClient == null || logs.isEmpty()) return

            val requestBody = getRequestBody(logs.logs)

            logs.clear()

            val response = httpClient.post(baseUrl) {
                url {
                    getHeaders().forEach { (key, value) ->
                        parameters.append(key, value)
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }

            if (response.status == HttpStatusCode.OK) {
                consoleLogger.log(LogLevel.INFO, tag, "Log successfully sent to $baseUrl")
            } else {
                consoleLogger.log(LogLevel.ERROR, tag, "Failed to send log to $baseUrl")
            }
        }
    }

    protected abstract fun getRequestBody(logs: Array<LogData>): Any?
    protected abstract fun getHeaders(): Map<String, String>

}