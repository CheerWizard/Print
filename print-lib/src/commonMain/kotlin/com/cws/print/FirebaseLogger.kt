package com.cws.print

import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class FirebaseLogger(
    val clientId: String,
    val apiSecret: String,
    val measurementId: String
) : NetworkLogger() {

    companion object {
        private const val CLIENT_ID = "client_id"
        private const val MEASUREMENT_ID = "measurement_id"
        private const val API_SECRET = "api_secret"
    }

    override val tag: String = "FirebaseLogger"
    override val sendPeriod: Duration = 10.seconds
    override val baseUrl: String = "https://www.google-analytics.com/mp/collect"

    override fun getRequestBody(logs: Array<LogData>): Any? {
        return buildJsonObject {
            put(CLIENT_ID, clientId)
            putJsonArray("events") {
                addJsonObject {
                    logs.map { log ->
                        put("name", "log")
                        putJsonObject("params") {
                            put("timestamp", log.timestamp)
                            put("level", log.level.name)
                            put("tag", log.tag)
                            put("message", log.message)
                            log.exception?.let { exception ->
                                put("exception", exception.message)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getHeaders(): Map<String, String> = mapOf(
        MEASUREMENT_ID to measurementId,
        API_SECRET to apiSecret,
    )

}