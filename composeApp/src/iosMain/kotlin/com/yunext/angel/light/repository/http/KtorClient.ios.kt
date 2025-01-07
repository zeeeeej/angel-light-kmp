package com.yunext.angel.light.repository.http

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.darwin.Darwin

actual fun myHttpClient(config:HttpClientConfig<out HttpClientEngineConfig>.()->Unit): HttpClient {
   return HttpClient(Darwin){
       config()
       engine {
           configureRequest {
               setAllowsCellularAccess(true)
           }
       }
   }
}
