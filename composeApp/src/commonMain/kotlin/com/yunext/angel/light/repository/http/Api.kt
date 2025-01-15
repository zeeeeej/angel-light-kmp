package com.yunext.angel.light.repository.http

import com.yunext.angel.light.BuildConfigX
import com.yunext.angel.light.repository.http.resp.CheckResp
import com.yunext.angel.light.repository.http.resp.UserResp
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

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
        @Query("type") type: String,
    ): HttpResp<CheckResp>

    @POST(Finish)
    @Headers("Content-Type: application/json")
    suspend fun finish(
        @Header(TOKEN) token: String,
        @Body body: Map<String, String>,
    ): HttpRespDataEmpty

}


class Api {

    companion object {
        private  val HOST = BuildConfigX.HOST
        fun img(id: String) = "$HOST$IMG_PATH$id"
        val apiService by lazy {
            //KtorClient.httpClient(HOST).create<ApiService>()
            KtorClient.httpClient(HOST).createApiService()
        }
    }
}

