package com.yunext.angel.light.repository.http

import de.jensklingenberg.ktorfit.Ktorfit
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun myHttpClient(config: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit): HttpClient

object KtorClient {
    private const val TAG = "_ktor_"
    private const val TIME_OUT = 15_000L
    private val ktorClient = myHttpClient {
        cfg()
    }

    private fun HttpClientConfig<out HttpClientEngineConfig>.cfg(
        tag: String = TAG,
        timeout: Long = TIME_OUT
    ) {
        engine {

        }
        install(ContentNegotiation) {
            // NoTransformationFoundException
            // https://ktor.io/docs/faq.html#no-transformation-found-exception
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = timeout
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.d(tag = tag) { message }
                }
            }
            level = LogLevel.ALL
//                filter { request ->
//                    request.url.host.contains("ktor.io")
//                }
//                sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }

    fun httpClient(baseUrl: String): Ktorfit {
        return Ktorfit.Builder().httpClient(ktorClient)
            .baseUrl(baseUrl)
            .build()
    }
}
