package com.cws.print

import io.ktor.client.HttpClient
import io.ktor.client.engine.curl.Curl
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

actual fun provideHttpClient(): HttpClient = HttpClient(Curl) {
    install(ContentNegotiation) {
        json()
    }
}