package com.yunext.angel.light.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.angel.light.BuildConfigX
import com.yunext.angel.light.BuildConfigX.IGNORE_CHECK_CODE
import com.yunext.angel.light.common.HDResult
import com.yunext.angel.light.common.throwableCaseOf
import com.yunext.angel.light.common.throwableOf
import com.yunext.angel.light.domain.Empty
import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.domain.poly.ProductModel
import com.yunext.angel.light.domain.poly.ProductType
import com.yunext.angel.light.domain.poly.ScanResultVo
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.repo.AppRepo
import com.yunext.angel.light.ui.vo.Packet
import com.yunext.kotlin.kmp.common.domain.Effect
import com.yunext.kotlin.kmp.common.domain.effectFail
import com.yunext.kotlin.kmp.common.domain.effectIdle
import com.yunext.kotlin.kmp.common.domain.effectProgress
import com.yunext.kotlin.kmp.common.domain.effectSuccess
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ScanState(
    val user: User = User.Empty,
    val effect: Effect<Unit, Pair<ScanResultVo, Packet>> = effectIdle(),
    val packet: Packet,
)

@OptIn(FlowPreview::class)
class ScanViewModel(private val appRepo: AppRepo, private val packet: Packet) : ViewModel() {
    private val user = appRepo.user
    private val userState = user.stateIn(viewModelScope, SharingStarted.Eagerly, User.Empty)
    private val effect: MutableStateFlow<Effect<Unit, Pair<ScanResultVo, Packet>>> =
        MutableStateFlow(effectIdle())
    val state: Flow<ScanState> = combine(user, effect) { u, e ->
        Napier.e { "ScanViewModel state changed :$u $e" }
        ScanState(user = u, effect = e, packet = packet)
    }


    @Deprecated("use v2")
    private fun checkV1(code: String, productCOde: String, type: ProductType) {
        val msg = when (type) {
            ProductType.PeiJian -> "找不到配件码${code}信息"
            ProductType.Device -> "找不到物流码${code}对应配件码信息"
        }
        viewModelScope.launch {
            try {
                effect.value = effectIdle()
                effect.value = effectProgress()
                val token = userState.value.token
                val result = withContext(Dispatchers.IO) {
                    appRepo.check(
                        token,
                        code = code,
                        type = type
                    )
                }

                when (result) {
                    is HDResult.Fail -> {
                        if (!IGNORE_CHECK_CODE) {
                            effect.value = effectFail(throwableOf { msg })
                        } else {
                            effect.value =
                                effectSuccess(
                                    output =
                                    ScanResultVo(code, "", "", "", "", "", "") to packet
                                )

                        }
                    }

                    is HDResult.Success -> {
                        when (type) {
                            ProductType.PeiJian -> if (result.data.peiJianCode == code) {
                                effect.value = effectSuccess(result.data to packet)
                            } else {
                                effect.value = effectFail(throwableOf { "错误的配件码" })
                            }

                            ProductType.Device -> effect.value =
                                effectSuccess(result.data to packet)

                        }

                    }
                }

            } catch (e: Throwable) {
                e.printStackTrace()
                effect.value = effectFail(throwableCaseOf(e))
            } finally {
//                effect.value = effectIdle()
            }
        }
    }


    private fun checkV2(code: String, productCOde: String, type: ProductType) {
        viewModelScope.launch {
            try {
                effect.value = effectIdle()
                effect.value = effectProgress()
                val token = userState.value.token
                val result = withContext(Dispatchers.IO) {
                    appRepo.check(
                        token,
                        code = code,
                        type = type
                    )
                }
                when (result) {
                    is HDResult.Fail -> {
                        if (!IGNORE_CHECK_CODE) {
                            val msg = when (type) {
                                ProductType.PeiJian -> "找不到配件码${code}信息"
                                ProductType.Device -> "找不到物流码${code}对应配件码信息"
                            }
                            effect.value = effectFail(throwableOf { msg })
                        } else {
                            effect.value =
                                effectSuccess(
                                    output = ScanResultVo(
                                        code,
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        ""
                                    ) to packet
                                )
                        }
                    }

                    is HDResult.Success -> {
                        when (type) {
                            ProductType.PeiJian -> if (result.data.peiJianCode == code) {
                                val newPacket = Packet(
                                    product = Product(
                                        code = result.data.productCode,
                                        name = result.data.productName
                                    ), productModel = ProductModel(
                                        id = "",
                                        name = result.data.modelName,
                                        identifier = result.data.identifier,
                                        img = result.data.img,
                                    ), type = ProductType.PeiJian
                                )
                                effect.value = effectSuccess(result.data to newPacket)
                            } else {
                                effect.value = effectFail(throwableOf { "错误的配件码" })
                            }

                            ProductType.Device -> {
                                val newPacket = Packet(
                                    product = Product(
                                        code = result.data.productCode,
                                        name = result.data.productName
                                    ), productModel = ProductModel(
                                        id = "",
                                        name = result.data.modelName,
                                        identifier = result.data.identifier,
                                        img = result.data.img,
                                    ), type = ProductType.Device
                                )
                                effect.value = effectSuccess(result.data to newPacket)
                            }
                        }
                    }
                }

            } catch (e: Throwable) {
                e.printStackTrace()
                effect.value = effectFail(throwableCaseOf(e))
            } finally {
//                effect.value = effectIdle()
            }
        }
    }


    /**
     *
     */
    private fun checkInternal(code: String, productCOde: String, productType: ProductType) {
        if (BuildConfigX.V2) {
            checkV2(code = code, productCOde = productCOde, productType)
        } else {
            checkV1(code = code, productCOde = productCOde, productType)
        }
    }

    private var checkWithComposeJob: Job? = null
    private val channel: Channel<Pair<String, Packet>> = Channel()

    init {
        viewModelScope.launch {
            channel.receiveAsFlow()
                .sample(DELAY)
                .collect { emitData ->
                    if (checkWithComposeJob?.isActive == true) return@collect
                    checkWithComposeJob = viewModelScope.launch {
                        try {
                            val (result, packet) = emitData
                            val (p, pm) = packet
                            checkInternal(result, pm.identifier, packet.type)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                }
        }
    }

    fun check(result: String, packet: Packet) {
        Napier.d {
            "ScanViewModel check $result $packet"
        }
        viewModelScope.launch {
            channel.send(result to packet)
        }
    }

    companion object {
        internal const val DELAY = 2000L
    }
}
