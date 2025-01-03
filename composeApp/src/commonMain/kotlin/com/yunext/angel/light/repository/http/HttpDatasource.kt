package com.yunext.angel.light.repository.http

import com.yunext.angel.light.common.HDResult
import com.yunext.angel.light.domain.FinishReq
import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.domain.poly.ProductModel
import com.yunext.angel.light.domain.poly.ProductType
import com.yunext.angel.light.domain.poly.ScanResult
import com.yunext.angel.light.domain.poly.value
import com.yunext.angel.light.repository.http.resp.UserResp
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.Headers
import io.ktor.http.content.PartData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface HttpDatasource {
    suspend fun login(user: String, pwd: String): HDResult<UserResp>
    suspend fun logout(token: String): HDResult<Boolean>
    suspend fun series(token: String): HDResult<List<Product>>
    suspend fun models(token: String, seriesId: String): HDResult<List<ProductModel>>
    suspend fun check(token: String, code: String, type: ProductType): HDResult<ScanResult>
    suspend fun finish(
        token: String,
        req: FinishReq
    ): HDResult<Boolean>
}

class HttpDatasourceImpl : HttpDatasource {
    private val apiService: ApiService = Api.apiService
    private val json = Json {
        encodeDefaults = true
        allowStructuredMapKeys = true
    }

    override suspend fun login(user: String, pwd: String): HDResult<UserResp> {
        return try {
            val httpResp = apiService.login(account = user, pwd = pwd)
            if (httpResp.success && httpResp.data != null) {
                HDResult.Success(httpResp.data)
            } else {
                HDResult.Fail(ApiException(msg = httpResp.msg.ifEmpty { "登录失败！" }))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            HDResult.Fail(ApiException(msg = "http请求错误 ${e.message}"))
        }
    }

    override suspend fun logout(token: String): HDResult<Boolean> {
        return try {
            val httpResp = apiService.logout(token = token)
            if (httpResp.success) {
                HDResult.Success(true)
            } else {
                HDResult.Fail(
                    if (httpResp.tokenExpired) {
                        TokenException()
                    } else ApiException(
                        code = httpResp.code,
                        msg = httpResp.msg.ifEmpty { "退出登录失败！" })
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            HDResult.Fail(ApiException(msg = "http请求错误 ${e.message}"))
        }
    }

    override suspend fun series(token: String): HDResult<List<Product>> {
        TODO()
//        return try {
//            val httpResp = apiService.series(token = token)
//            if (httpResp.success) {
//                HDResult.Success(httpResp.data.map {
//                    Product(
//                        code = it.code ?: throw IllegalStateException("code is null"),
//                        name = it.name ?: ""
//                    )
//                })
//            } else {
//                HDResult.Fail(
//                    if (httpResp.tokenExpired) {
//                        onTokenExpired()
//                        TokenException()
//                    } else ApiException(
//                        code = httpResp.code,
//                        msg = httpResp.msg.ifEmpty { "series失败！" })
//                )
//            }
//        } catch (e: Exception) {
//            HDResult.Fail(ApiException(msg = "http请求错误 ${e.localizedMessage}"))
//        }
    }

    override suspend fun models(token: String, seriesId: String): HDResult<List<ProductModel>> {
        TODO()
//        return try {
//            val httpResp = apiService.models(token = token, seriesId = seriesId)
//            if (httpResp.success) {
//                HDResult.Success(httpResp.data.map {
//                    ProductModel(
//                        id = it.id ?: throw IllegalStateException("id is null"),
//                        identifier = it.identifier
//                            ?: throw IllegalStateException("identifier is null"),
//                        img = it.img ?: "",
//                        name = it.name ?: ""
//                    )
//                })
//            } else {
//                HDResult.Fail(
//                    if (httpResp.tokenExpired) {
//                        onTokenExpired()
//                        TokenException()
//                    } else ApiException(
//                        code = httpResp.code,
//                        msg = httpResp.msg.ifEmpty { "models失败！" })
//                )
//            }
//        } catch (e: Exception) {
//            HDResult.Fail(ApiException(msg = "http请求错误 ${e.localizedMessage}"))
//        }
    }

    override suspend fun check(
        token: String,
        code: String,
        type: ProductType
    ): HDResult<ScanResult> {
        return try {
            val httpResp = apiService.check(token = token, code = code, type = type.value)
            val data = httpResp.data
            if (httpResp.success && data != null) {
                HDResult.Success(
                    ScanResult(
                        peiJianCode = data.componentCode ?: "",
                        wuLiuCode = data.code ?: code,
                        productCode = data.productCode ?: "",
                        productName = data.seriesName ?: "", // 无界大通量
                        identifier = data.productCode ?: "",
                        img = data.productImg ?: "",
                        modelName = data.productName ?: "", // 系列和产品name和服务器定义是错开的
                    )
                )
            } else {
                HDResult.Fail(
                    if (httpResp.tokenExpired) {
                        TokenException()
                    } else ApiException(
                        code = httpResp.code,
                        msg = httpResp.msg.ifEmpty { "检查失败！" })
                )
            }
        } catch (e: Exception) {
            HDResult.Fail(ApiException(msg = "http请求错误 ${e.message}"))
        }
    }

    override suspend fun finish(
        token: String,
        req: FinishReq
    ): HDResult<Boolean> {
        return try {
            val map: Map<String, String> = when (req) {
                is FinishReq.Device -> mapOf(
                    "code" to req.code,
                    "componentCode" to req.componentCode,
                    "productCode" to req.productCode,
                    "result" to req.result,
                    "type" to ProductType.Device.value,
                )

                is FinishReq.PeiJian -> mapOf(
                    "componentCode" to req.componentCode,
                    "result" to req.result,
                    "type" to ProductType.PeiJian.value,
                )
            }
            val httpResp = apiService.finish(token = token, body = map)
            if (httpResp.success) {
                HDResult.Success(true)
            } else {

                HDResult.Fail(
                    if (httpResp.tokenExpired) {
                        TokenException()
                    } else
                        ApiException(
                            code = httpResp.code,
                            msg = httpResp.msg.ifEmpty { "完成失败！" })
                )
            }
        } catch (e: Exception) {
            HDResult.Fail(ApiException(msg = "http请求错误 ${e.message}"))
        }
    }

}