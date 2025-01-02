package com.yunext.angel.light.repository.http

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.cio.CIO

actual fun myHttpClient(config: HttpClientConfig<out HttpClientEngineConfig>.()->Unit): HttpClient {
    return HttpClient(CIO) {
        config()
    }
}