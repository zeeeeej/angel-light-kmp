package com.yunext.angel.light.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.angel.light.BuildConfigX
import com.yunext.angel.light.common.HDResult
import com.yunext.angel.light.common.throwableOf
import com.yunext.angel.light.domain.DeviceProduct
import com.yunext.angel.light.domain.PeiJianProduct
import com.yunext.angel.light.domain.isEmpty
import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.domain.poly.ProductModel
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.repository.AppRepo
import com.yunext.kotlin.kmp.common.domain.Effect
import com.yunext.kotlin.kmp.common.domain.effectFail
import com.yunext.kotlin.kmp.common.domain.effectIdle
import com.yunext.kotlin.kmp.common.domain.effectProgress
import com.yunext.kotlin.kmp.common.domain.effectSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MainState(
    val user: User ,
    val products: List<Product> = emptyList(),
    val productModels: List<ProductModel> = emptyList(),
    val logoutEffect: Effect<Unit, Boolean> = effectIdle(),
)


class MainViewModel(private val appRepo: AppRepo, val user: User) : ViewModel() {
    init {
        require(!user.isEmpty) {
            "user.isEmpty 怎么可能进入主页呢？"
        }
    }

    private val effect: MutableStateFlow<Effect<Unit, Boolean>> = MutableStateFlow(effectIdle())
    private val products: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())
    private val productModels: MutableStateFlow<List<ProductModel>> = MutableStateFlow(emptyList())
//    private val user: Flow<User> = appRepo.user

    val state: Flow<MainState> = combine(effect, products, productModels) { e, ps, pms ->
        MainState(user = user, logoutEffect = e, products = ps, productModels = pms)
    }


    private val selectedProduct: Product? = null

    fun listProductModel(product: Product) {
        listProductModelV2(product)
    }

    private fun listProductModelV2(product: Product) {
    }

    fun listProduct() {
        if (BuildConfigX.V2) {
            listProductV2()
        } else {
            listProductV1()
        }

    }

    @Deprecated("直接扫描")
    private fun listProductV1() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    appRepo.series(user.token)
                }
                when (result) {
                    is HDResult.Fail -> {

                    }

                    is HDResult.Success -> {
                        products.value = buildList<Product> {
                            addAll(result.data)
                            add(Product.PeiJianProduct)
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun listProductV2() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    listOf(Product.DeviceProduct, Product.PeiJianProduct)
                }
                products.value = result
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            logoutInternal(user.token)
        }
    }

    private suspend fun logoutInternal(token: String?) {
        try {
            effect.value = effectProgress()

            val result = withContext(Dispatchers.IO) {
                if (token.isNullOrBlank()) {
                    HDResult.Success(true)
                } else
                    appRepo.logout(token = token)
            }
            when (result) {
                is HDResult.Fail -> {
                    effect.value = effectFail(throwableOf { "退出失败" })
                }

                is HDResult.Success -> {
                    appRepo.clearUser()
                    effect.value = effectSuccess(true)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            effect.value = effectFail(e)
        }
    }
}
