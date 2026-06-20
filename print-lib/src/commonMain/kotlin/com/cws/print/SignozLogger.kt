package com.cws.print

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class OtlpLogRequest(val resourceLogs: List<ResourceLogs>)

@Serializable
data class ResourceLogs(
    val resource: Resource,
    val scopeLogs: List<ScopeLogs>
)

@Serializable
data class Resource(
    val attributes: List<Attribute> = emptyList()
)

@Serializable
data class ScopeLogs(val logRecords: List<LogRecord>)

@Serializable
data class LogRecord(
    val timeUnixNano: String,
    val severityText: String,
    val body: Body,
    val attributes: List<Attribute> = emptyList()
)

@Serializable
data class Body(val stringValue: String)

@Serializable
data class Attribute(
    val key: String,
    val value: AttributeValue
)

@Serializable
data class AttributeValue(val stringValue: String)

class SignozLogger(
    host: String,
    logLevel: LogLevel = LogLevel.WARNING
) : NetworkLogger(logLevel) {

    override val baseUrl = "http://$host:4318/v1/logs"
    override val tag: String = "SignozLogger"
    override val sendPeriod: Duration = 10.seconds

    override fun getRequestBody(logs: Array<LogData>): Any? {
        val logRecords = logs.map { log ->
            LogRecord(
                timeUnixNano = log.timestamp.toString(),
                severityText = log.level.toString(),
                body = Body(log.message)
            )
        }

        return OtlpLogRequest(
            resourceLogs = listOf(
                ResourceLogs(
                    resource = Resource(
                        attributes = listOf(
                            Attribute("service.name", AttributeValue("printer"))
                        )
                    ),
                    scopeLogs = listOf(ScopeLogs(logRecords))
                )
            )
        )
    }

    override fun getHeaders(): Map<String, String> = emptyMap()

}