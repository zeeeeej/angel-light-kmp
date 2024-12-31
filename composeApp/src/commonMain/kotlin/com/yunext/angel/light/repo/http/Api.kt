package com.yunext.angel.light.repo.http

import com.yunext.angel.light.BuildConfigX

interface ApiService {
//    @FormUrlEncoded
//    @POST("/api/app/account/login")
//    suspend fun login(
//        @Field("account") account: String,
//        @Field("pwd") password: String,
//        @Field("captchaVerification") captchaVerification: String = "",
//    ): HttpResp<UserResp>
//
//    // 退出系统
//    @GET("/api/app/account/logout")
//    suspend fun logout(
//        @Header("token") token: String,
//    ): HttpResp<Any?>
//
//    @GET("/api/app/product/series/select")
//    suspend fun series(
//        @Header("token") token: String,
//    ): HttpResp<List<ProductResp>>
//
//    @GET("/api/app/product/list")
//    suspend fun models(
//        @Header("token") token: String,
//        @Query("seriesId") seriesId: String,
//    ): HttpResp<List<ProductModelResp>>
//
//    // 校验设备是否存在
//
//    @GET("/api/app/chance/device/check")
//    suspend fun check(
//        @Header("token") token: String,
//        @Query("code") code: String,
//        @Query("type") type: String,
//    ): HttpResp<CheckResp>
//
//    // 检测完成
//    @Headers("Content-Type: application/json")
//    @POST("/api/app/chance/finish")
//    suspend fun finish(
//        @Header("token") token: String,
//        @Body body: RequestBody,
////        @Query("componentCode") componentCode: String,
////        @Query("productCode") productCode: String,
////        @Query("result") result: String,
//    ): HttpResp<Any?>
}


class Api {

    companion object {
        private const val HOST = BuildConfigX.HOST
        private const val TOKEN = "Authorization"
        private const val TIME_OUT = 15_000L

        private const val IMG_PATH = "api/file/read?id="

        fun img(id: String) = "$HOST$IMG_PATH$id"

//        private val retrofit by lazy {
//            Retrofit.Builder()
//                .client(
//                    createOKHttpClientBuilder()
//                        .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
//                        .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
//                        .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
//                        .build()
//                )
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(HOST).build()
//        }
//
//        val apiService by lazy {
//            retrofit.create(ApiService::class.java)
//        }
    }
}

data class ApiReq(
    val path: String,
    val type: ReqType
)


enum class ReqType {
    GET,
    POST
    ;
}

//private fun createOKHttpClientBuilder(): OkHttpClient.Builder {
//    val builder = OkHttpClient.Builder()
//    val logging = HttpLoggingInterceptor().apply {
//        setLevel(HttpLoggingInterceptor.Level.BODY)
//    }
//    builder.eventListener(object : EventListener() {
//        override fun callStart(call: Call) {
//            super.callStart(call)
//        }
//
//        override fun callEnd(call: Call) {
//            super.callEnd(call)
//
//        }
//
//        override fun callFailed(call: Call, ioe: IOException) {
//            super.callFailed(call, ioe)
//        }
//    })
//    //if (BuildConfig.DEV) {
//    logging.level = HttpLoggingInterceptor.Level.BODY
//    //} else {
//    //    logging.level = HttpLoggingInterceptor.Level.NONE
//    //}
//    builder
//        //.addInterceptor(TokenInvalidInterceptor.INSTANCE)
//        .addInterceptor(logging)
//    //.addInterceptor(LanguageInterceptor())
//    return builder
//}