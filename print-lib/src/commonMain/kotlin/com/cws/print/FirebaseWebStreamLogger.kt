package com.cws.print

import io.ktor.http.ContentType
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// Logs as analytics events to Firebase Web Stream data stream type.
class FirebaseWebStreamLogger(
    val clientId: String,
    val apiSecret: String,
    val measurementId: String,
    override val sendPeriod: Duration = 10.seconds
) : NetworkLogger() {

    companion object {
        private const val CLIENT_ID = "client_id"
        private const val MEASUREMENT_ID = "measurement_id"
        private const val API_SECRET = "api_secret"
    }

    override val tag: String = "FirebaseLogger"
    override val baseUrl: String = "https://www.google-analytics.com/mp/collect"

    override fun getRequestBody(logs: Array<NetworkLogEntry>): Any {
        return buildJsonObject {
            put(CLIENT_ID, clientId)
            putJsonArray("events") {
                logs.forEach { log ->
                    addJsonObject {
                        put("name", "log_${log.level.name.lowercase()}")
                        put("timestamp_micros", log.timestamp * 1000)
                        putJsonObject("params") {
                            put("session_id", "session_$clientId")
                            put("engagement_time_msec", 1) // placeholder for now
                            put("dateTime", log.timestamp.milliseconds.formatDateTime("dd.MM.YYYY HH:mm:ss"))
                            put("level", log.level.name)
                            put("tag", log.tag)
                            put("message", log.message.take(100)) // TODO: need to confirm with Firebase Analytics docs the limit of message length
                            log.exception?.let { exception ->
                                put("exception", exception.message?.take(100) ?: "Unknown error")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getQueryParams(): Map<String, String> = mapOf(
        MEASUREMENT_ID to measurementId,
        API_SECRET to apiSecret,
    )

    override fun getContentType(): ContentType = ContentType.Text.Plain

}