package com.yunext.angel.light.repo

import com.yunext.angel.light.common.HDResult
import com.yunext.angel.light.common.throwableOf
import com.yunext.angel.light.domain.Empty
import com.yunext.angel.light.domain.FinishReq
import com.yunext.angel.light.domain.UserDomain
import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.domain.poly.ProductModel
import com.yunext.angel.light.domain.poly.ScanResultVo
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.repo.http.httpSuccess
import com.yunext.angel.light.domain.poly.ProductType
import com.yunext.angel.light.repo.http.httpFail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow


class AppRepo(
    private val userDatasource: UserDatasource,
) : UserDomain {
    override val user: Flow<User> = userDatasource.user

    override suspend fun saveUser(user: User) {
        userDatasource.saveUser(user)
    }

    override suspend fun clearUser() {
        userDatasource.clear()
    }

    override suspend fun login(user: String, pwd: String): HDResult<User> {
        delay(1000)
        return httpSuccess(User.Empty.copy(username = "zeej", token = "123456"))
    }

    override suspend fun logout(token: String): HDResult<Boolean> {
        delay(2000)
        return httpSuccess(true)
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
    ): HDResult<ScanResultVo> {
        delay(2000)
//        return httpFail(throwableOf("失败"))
        return httpSuccess(
            ScanResultVo(
                peiJianCode = "",
                wuLiuCode = "",
                productCode = "",
                productName = "",
                identifier = "",
                img = "",
                modelName = ""
            )
        )
    }

    override suspend fun finish(token: String, req: FinishReq): HDResult<Boolean> {
        TODO("Not yet implemented")
    }


}