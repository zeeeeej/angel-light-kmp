package com.yunext.angel.light.repository.http

import com.yunext.angel.light.BuildConfigX
import com.yunext.angel.light.repository.http.resp.CheckResp
import com.yunext.angel.light.repository.http.resp.UserResp
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// paths
private const val Login = "api/app/account/login"
private const val Logout = "api/app/account/logout"
private const val DeviceCheck = "api/app/chance/device/check"
private const val Finish = "api/app/chance/finish"
private const val TOKEN = "token"
private const val IMG_PATH = "api/file/read?id="

interface ApiService {
    @POST(Login)
    @FormUrlEncoded
    suspend fun login(
        @Field("account") account: String,
        @Field("pwd") pwd: String,
        @Field("captchaVerification") captchaVerification: String = "",
    ): HttpResp<UserResp>

    @GET(Logout)
    @FormUrlEncoded
    suspend fun logout(
        @Header(TOKEN) token: String,
    ): HttpRespDataEmpty

    @GET(DeviceCheck)
    suspend fun check(
        @Header(TOKEN) token: String,
        @Query("code") code: String,
        @Field("type") type: String,
    ): HttpResp<CheckResp>

    @POST(Finish)
    @Headers("Content-Type: application/json")
    suspend fun finish(
        @Header(TOKEN) token: String,
        @Body body: MultiPartFormDataContent,
    ): HttpRespDataEmpty

}


class Api {

    companion object {
        private const val HOST = BuildConfigX.HOST
        private const val TIME_OUT = 15_000L
        fun img(id: String) = "$HOST$IMG_PATH$id"
        private const val TAG = "_ktor_"
        private val ktorClient = HttpClient(CIO) {
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
                connectTimeoutMillis = TIME_OUT
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(tag = TAG) { message }
                    }
                }
                level = LogLevel.ALL
//                filter { request ->
//                    request.url.host.contains("ktor.io")
//                }
//                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
        }

        private val httpClient by lazy {
            Ktorfit.Builder().httpClient(ktorClient)
                .baseUrl(HOST)
                .build()
        }

        val apiService by lazy {
            httpClient.create<ApiService>()
        }
    }
}