package com.yunext.angel.light.repository

import com.yunext.angel.light.common.HDResult
import com.yunext.angel.light.domain.FinishReq
import com.yunext.angel.light.domain.UserDomain
import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.domain.poly.ProductModel
import com.yunext.angel.light.domain.poly.ScanResult
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.domain.poly.ProductType
import com.yunext.angel.light.repository.http.HttpDatasource
import com.yunext.angel.light.repository.http.TokenException
import com.yunext.angel.light.repository.http.map
import com.yunext.angel.light.repository.http.resp.convert
import com.yunext.angel.light.repository.sp.UserDatasource
import kotlinx.coroutines.flow.Flow


class AppRepo(
    private val userDatasource: UserDatasource,
    private val httpDatasource: HttpDatasource
) : UserDomain {
    override val user: Flow<User> = userDatasource.user

    override suspend fun saveUser(user: User) {
        userDatasource.saveUser(user)
    }

    override suspend fun clearUser() {
        userDatasource.clear()
    }

    override suspend fun login(user: String, pwd: String): HDResult<User> {
        val login = httpDatasource.login(user, pwd)
        val result =  login.map {
            it.convert() ?: throw IllegalArgumentException("user is null")
        }
        when(result){
            is HDResult.Fail -> {}
            is HDResult.Success -> saveUser(result.data)
        }

        return result
        //return httpSuccess(User.Empty.copy(username = "zeej", token = "123456"))
    }

    override suspend fun logout(token: String): HDResult<Unit> {
        when (val logout = httpDatasource.logout(token)) {
            is HDResult.Fail -> {
                if (logout.error is TokenException) {
                    clearUser()
                    return HDResult.Success(Unit)
                } else {
                    return logout
                }
            }

            is HDResult.Success -> {
                clearUser()
                return HDResult.Success(Unit)
            }
        }
    }

    override suspend fun series(token: String): HDResult<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun models(token: String, seriesId: String): HDResult<List<ProductModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun check(
        token: String,
        code: String,
        type: ProductType
    ): HDResult<ScanResult> {
        return httpDatasource.check(token, code, type).let {
            r->
            when(r){
                is HDResult.Fail -> if (r.error is TokenException){
                    clearUser()
                }
                is HDResult.Success -> {}
            }
            r
        }
//        delay(2000)
//        return httpFail(throwableOf("失败"))
//        return httpSuccess(
//            ScanResultVo(
//                peiJianCode = "",
//                wuLiuCode = "",
//                productCode = "",
//                productName = "",
//                identifier = "",
//                img = "",
//                modelName = ""
//            )
//        )
    }

    override suspend fun finish(token: String, req: FinishReq): HDResult<Boolean> {
        return httpDatasource.finish(token, req)
    }


}