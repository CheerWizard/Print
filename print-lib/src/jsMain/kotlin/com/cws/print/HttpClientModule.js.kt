package com.cws.print

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import kotlin.js.json

actual fun provideHttpClient(): HttpClient = HttpClient(Js) {
    install(ContentNegotiation) {
        json()
    }
}