package com.yunext.angel.light.domain

import com.yunext.angel.light.common.HDResult
import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.domain.poly.ProductModel
import com.yunext.angel.light.domain.poly.ScanResultVo
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.domain.poly.ProductType
import com.yunext.angel.light.repository.http.HttpDatasource
import kotlinx.coroutines.flow.Flow

val User.Companion.Empty by lazy {
    User("___IDLE_USER___", "", "", "", "", "")
}

val Product.Companion.PeiJianProduct: Product by lazy {
    Product(code = "com.yunnext.angel.light.domain.peijian.product", "配件")
}
val Product.Companion.DeviceProduct by lazy {
    Product(code = "com.yunnext.angel.light.domain.device.product", "设备")
}

val ProductModel.Companion.EmptyModel: ProductModel by lazy {
    ProductModel(
        id = "com.yunnext.angel.light.domain.peijian.id",
        name = "com.yunnext.angel.light.domain.peijian.name",
        identifier = "com.yunnext.angel.light.domain.peijian.identifier",
        img = ""
    )
}

val ProductModel.Companion.EmptyDevice: ProductModel by lazy {
    ProductModel(
        id = "com.yunnext.angel.light.domain.device.id",
        name = "com.yunnext.angel.light.domain.device.name",
        identifier = "com.yunnext.angel.light.domain.device.identifier",
        img = ""
    )
}

val User.isEmpty: Boolean
    get() = this == User.Empty

interface UserDomain {

    val user: Flow<User>

    suspend fun saveUser(user: User)
    suspend fun clearUser()

    suspend fun login(user: String, pwd: String): HDResult<User>
    suspend fun logout(token: String): HDResult<Unit>
    suspend fun series(token: String): HDResult<List<Product>>
    suspend fun models(token: String, seriesId: String): HDResult<List<ProductModel>>
    suspend fun check(token: String, code: String, type: ProductType): HDResult<ScanResultVo>
    suspend fun finish(
        token: String,
        req: FinishReq
    ): HDResult<Boolean>


}