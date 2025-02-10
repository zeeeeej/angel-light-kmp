package com.yunext.angel.light.ui.viewmodel

import BleEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunext.angel.light.common.HDResult
import com.yunext.angel.light.common.throwableOf
import com.yunext.angel.light.di.myJson
import com.yunext.angel.light.domain.Empty
import com.yunext.angel.light.domain.FinishReq
import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.domain.poly.ProductResult
import com.yunext.angel.light.domain.poly.ProductType
import com.yunext.angel.light.domain.poly.ScanResult
import com.yunext.angel.light.domain.poly.User
import com.yunext.angel.light.repository.AppRepo
import com.yunext.angel.light.repository.ble.BleX
import com.yunext.angel.light.repository.ble.SetDeviceInfoKey
import com.yunext.angel.light.repository.ble.TslPropertyKey
import com.yunext.angel.light.repository.ble.datetimeFormat
import com.yunext.angel.light.repository.ble.text
import com.yunext.angel.light.repository.ble.unit
import com.yunext.angel.light.repository.ble.value
import com.yunext.angel.light.repository.json
import com.yunext.angel.light.ui.screen.ProductModelVo
import com.yunext.angel.light.ui.vo.ActionResult
import com.yunext.angel.light.ui.vo.BleLog
import com.yunext.angel.light.ui.vo.Packet
import com.yunext.angel.light.ui.vo.ProductionResult
import com.yunext.angel.light.ui.vo.PropertyVo
import com.yunext.kotlin.kmp.common.domain.Effect
import com.yunext.kotlin.kmp.common.domain.effectFail
import com.yunext.kotlin.kmp.common.domain.effectIdle
import com.yunext.kotlin.kmp.common.domain.effectProgress
import com.yunext.kotlin.kmp.common.domain.effectSuccess
import display
import io.github.aakira.napier.Napier

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString

typealias SimpleEffect = Effect<Unit, Unit>

data class ProductionState(
    val product: Product,
    val productModel: ProductModelVo,
    val productType: ProductType = ProductType.Device,
    val scanResult: ScanResult,

    val commitEffect: Effect<Unit, Unit> = effectIdle(),
    val bleLogs: List<BleLog> = emptyList(),
    val power: Boolean = false,
    val wash: Boolean = false,
    val properties: List<PropertyVo> = emptyList(),
    val connectEffect: Effect<Unit, Unit> = Effect.Idle,
    val connected: Boolean = false,
    val washEffect: Effect<Unit, Unit> = Effect.Idle,
    val powerEffect: Effect<Unit, Unit> = Effect.Idle,
    @Deprecated("commitEffect", replaceWith = ReplaceWith("commitEffect"))
    val resetEffect: Effect<Unit, Unit> = Effect.Idle,
    val productionEffect: Effect<Unit, Unit> = Effect.Idle,
    val washProductionResult: ProductionResult<String> = ProductionResult.Idle,
    val powerProductionResult: ProductionResult<String> = ProductionResult.Idle,
    val productionProductionResult: ProductionResult<String> = ProductionResult.Idle,
    @Deprecated("commitEffect", replaceWith = ReplaceWith("commitEffect"))
    val resetResult: ProductionResult<String> = ProductionResult.Idle,
    val toast: String = "",
    val debug: Boolean = false,
    val bleSimpleLogs: List<String> = emptyList()

)

@Suppress("UNCHECKED_CAST")
class ProductionViewModel(
    private val appRepo: AppRepo,
    private val sr: ScanResult,
    private val pt: Packet
) :
    ViewModel() {

    private val user = appRepo.user.stateIn(viewModelScope, SharingStarted.Eagerly, User.Empty)
    private val debug: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val commitEffect: MutableStateFlow<SimpleEffect> = MutableStateFlow(effectIdle())
    private val bleLogs: MutableStateFlow<List<BleLog>> = MutableStateFlow(emptyList())
    private val bleSimpleLogs: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val power: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val wash: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val properties: MutableStateFlow<List<PropertyVo>> = MutableStateFlow(emptyList())
    private val connectEffect: MutableStateFlow<SimpleEffect> = MutableStateFlow(effectIdle())
    private val connected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val washEffect: MutableStateFlow<SimpleEffect> = MutableStateFlow(effectIdle())
    private val powerEffect: MutableStateFlow<SimpleEffect> = MutableStateFlow(effectIdle())

    @Deprecated("use commitEffect")
    private val resetEffect: MutableStateFlow<SimpleEffect> = MutableStateFlow(effectIdle())
    private val productionEffect: MutableStateFlow<SimpleEffect> = MutableStateFlow(effectIdle())
    private val washProductionResult: MutableStateFlow<ProductionResult<String>> =
        MutableStateFlow(ProductionResult.Idle)
    private val powerProductionResult: MutableStateFlow<ProductionResult<String>> =
        MutableStateFlow(ProductionResult.Idle)
    private val productionProductionResult: MutableStateFlow<ProductionResult<String>> =
        MutableStateFlow(ProductionResult.Idle)

    @Deprecated("use commitEffect")
    private val resetResult: MutableStateFlow<ProductionResult<String>> =
        MutableStateFlow(ProductionResult.Idle)
    private val toast: MutableStateFlow<String> = MutableStateFlow("")

    val state: Flow<ProductionState> = combine(
        commitEffect,
        bleLogs,
        power,
        wash,
        properties,
        connectEffect,
        connected,
        washEffect,
        powerEffect,
        resetEffect,
        productionEffect,
        washProductionResult,
        powerProductionResult,
        productionProductionResult,
        resetResult,
        toast,
        debug, bleSimpleLogs
    ) { changed ->
        ProductionState(
            product = pt.product,
            productModel = pt.productModel,
            scanResult = sr,
            productType = pt.type,
            commitEffect = changed[0] as SimpleEffect,
            bleLogs = changed[1] as List<BleLog>,
            power = changed[2] as Boolean,
            wash = changed[3] as Boolean,
            properties = changed[4] as List<PropertyVo>,
            connectEffect = changed[5] as SimpleEffect,
            connected = changed[6] as Boolean,
            washEffect = changed[7] as SimpleEffect,
            powerEffect = changed[8] as SimpleEffect,
            resetEffect = changed[9] as SimpleEffect,
            productionEffect = changed[10] as SimpleEffect,
            washProductionResult = changed[11] as ProductionResult<String>,
            powerProductionResult = changed[12] as ProductionResult<String>,
            productionProductionResult = changed[13] as ProductionResult<String>,
            resetResult = changed[14] as ProductionResult<String>,
            toast = changed[15] as String,
            debug = changed[16] as Boolean,
            bleSimpleLogs = changed[17] as List<String>,
        )
    }

    private var first = true
    private val auto = false

    private var resetChannel: Channel<ActionResult<Boolean, String>>? = Channel()
    private var washChannel: Channel<ActionResult<Boolean, String>>? = Channel()
    private var powerChannel: Channel<ActionResult<Boolean, String>>? = Channel()
    private var productionChannel: Channel<ActionResult<BleEvent.Production, String>>? = Channel()

    private var doFirstAutoProductionJob: Job? = null
    private var autoConnectJob: Job? = null
    private var connectJob: Job? = null
    private var scanJob: Job? = null
    private var washJob: Job? = null
    private var powerJob: Job? = null
    private var productionJob: Job? = null
    private var resetJob: Job? = null

    init {
        autoConnect()
    }

     fun toast(msg: String = "") {
        toast.value = msg
    }

    fun clearToast() = toast()

    private fun clearJobs() {
        doFirstAutoProductionJob?.cancel()
        autoConnectJob?.cancel()
        connectJob?.cancel()
        scanJob?.cancel()
        washJob?.cancel()
        powerJob?.cancel()
        productionJob?.cancel()
        autoConnectJob = null
        connectJob = null
        scanJob = null
        washJob = null
        powerJob = null
        productionJob = null
        powerEffect.value = Effect.Idle
        washEffect.value = Effect.Idle
        productionEffect.value = Effect.Idle
        connectEffect.value = Effect.Idle
        commitEffect.value = Effect.Idle
    }


    private fun addLog(log: String) {
        val old = bleLogs.value
        bleLogs.value = listOf(BleLog(log)) + old
    }

    private fun addLog(log: BleLog) {
        val old = bleLogs.value
        bleLogs.value = listOf(log) + old
    }

    private fun clearLog() {
        bleLogs.value = emptyList()
    }

    private fun doFirstAutoProduction() {

        viewModelScope.launch(Dispatchers.Main) {
            if (first) {
                d("doFirstAutoProduction ==========>")
                first = false
                doFirstAutoProductionJob?.cancel()
                doFirstAutoProductionJob = viewModelScope.launch {
                    if (auto) {

                        delay(1000)
                        if (power.value) {
                            try {
                                wash(!wash.value)
                            } catch (e: Exception) {
                                d("doFirstAutoProduction 1 wash error $e")
                            }
                            d("doFirstAutoProduction 1 wash")
                            while (true) {
                                delay(500)
                                if (washProductionResult.value !is ProductionResult.Idle) {
                                    break
                                }
                            }
                        }

                        d("doFirstAutoProduction 2 power")
                        try {
                            power(!power.value)
                        } catch (e: Exception) {
                            d("doFirstAutoProduction 2 power error $e")
                        }
                        while (true) {
                            delay(500)
                            if (powerProductionResult.value !is ProductionResult.Idle) {
                                break
                            }
                        }
                        d("doFirstAutoProduction 3 production")
                        production()
                    }
                }.also {
                    it.invokeOnCompletion {
                        d("doFirstAutoProduction invokeOnCompletion")
                    }
                }
            }
        }
    }

    init {
        viewModelScope.launch {

//            launch {
//                while (isActive){
//                    delay(1000)
//                    addLog("=>${currentTime()}")
//                }
//            }

            launch {
                BleX.downChannel.receiveAsFlow().collect { bleEvent ->
                    addLog(bleEvent.toString())
                    when (bleEvent) {
                        is BleEvent.Authed -> {
                            connected.value = true
                            connectEffect.value = effectSuccess()
                        }

                        BleEvent.Connected -> {

                        }

                        is BleEvent.DeviceInfo -> {

                            val map = bleEvent.properties
                            val isOpen = map[TslPropertyKey.IsOpen]
                            if (isOpen != null) {
                                power.value = isOpen as Boolean
                                powerChannel?.trySend(ActionResult.Success(isOpen as Boolean))


                            }
                            val isWash = map[TslPropertyKey.WashState]
                            if (isWash != null) {
                                wash.value = isWash as Boolean
                                washChannel?.trySend(ActionResult.Success(isWash))
                            }


                            if (map.isNotEmpty()) {
                                val oldList = properties.value
                                val pList = map.map { (k, v) ->
                                    PropertyVo(
                                        key = k,
                                        unit = k.unit,
                                        value = k.value(v) ?: "",
                                        name = k.text,
                                    )
                                }
                                val newList = oldList.filter { t1 ->
                                    !pList.any { t2 ->
                                        t2.key == t1.key
                                    }
                                } + pList
                                properties.value = newList
                            }

                            doFirstAutoProduction()


                        }

                        is BleEvent.Disconnect -> {
                            connectEffect.value = effectFail(throwableOf { "disconnect" })
                            connected.value = false
                            //
                            clearJobs()
//                                if (it.isActiveDisConnected){
//                        reAutoConnect() // TODO
//                                }
                        }

                        is BleEvent.Error -> {
                            connectEffect.value = effectFail(throwableOf { bleEvent.msg })
                            connected.value = false
                            clearJobs()
//                        reAutoConnect()// TODO
                        }

//                        is BleEvent.Msg -> {
//                            addLog(bleEvent.msg)
//                        }

                        is BleEvent.Notify -> {}
                        is BleEvent.Version -> {}
                        is BleEvent.Production -> {
                            productionChannel?.trySend(ActionResult.Success((bleEvent)))
                        }

                        is BleEvent.SetDeviceResult -> {
                            // ...
                            val properties = bleEvent.properties
                            val result = properties[SetDeviceInfoKey.Set21]
                            Napier.d(tag = "blex") { "SetDeviceResult result:$result" }
                            if (result == true) {
                                resetChannel?.trySend(ActionResult.Success(true))
                            }
                        }

                        is BleEvent.Status -> {
                            bleSimpleLogs.value += "[状态]" + datetimeFormat { bleEvent.timestamp.toStr() } + " " + bleEvent.value
                        }

                        is BleEvent.Up -> {
                            bleSimpleLogs.value += "[发送]" + datetimeFormat { bleEvent.timestamp.toStr() } + " " + bleEvent.value
                        }

                        is BleEvent.Down -> {
                            bleSimpleLogs.value += "[接受]" + datetimeFormat { bleEvent.timestamp.toStr() } + " " + bleEvent.value
                        }


                        else -> {}
                    }
                }
            }

            launch {
                BleX.logChannel.receiveAsFlow().collect { bleEvent ->
                    addLog(bleEvent)
                }
            }

        }

    }

    override fun onCleared() {
        super.onCleared()
        BleX.stop()
    }

    private fun autoConnect() {
        connectEffect.value = effectProgress()

        BleX.start(sr.peiJianCode)
    }

    fun stop() {
        d("停止产测")
        clearJobs()
        BleX.stop()
    }

    fun wash(dest: Boolean) {
        if (!connected.value) {
            toast(TOAST_BLUETOOTH_DISCONNECT)
            return
        }
        washJob?.cancel()
        washJob = viewModelScope.launch {
            val timeoutJob = launch {
                delay(TIMEOUT + 1000)
                d("ProductionViewModel wash 超时")
                washChannel?.trySend(ActionResult.TimeOut)
            }.also {
                it.invokeOnCompletion {
                    d("ProductionViewModel wash 超时任务关闭")

                }
            }
            d("ProductionViewModel wash $dest ===>")
            washEffect.value = effectProgress()
            washProductionResult.value = ProductionResult.Idle
            val channel = Channel<ActionResult<Boolean, String>>(1)
            washChannel = channel
            BleX.wash(dest)
            val receiveData = channel.receive()
            d("ProductionViewModel receive wash =$receiveData")
            channel.cancel()
            washChannel = null
            timeoutJob.cancel()
            delay(DELAY)
            val failBlock = { msg: String ->

                washEffect.value = effectFail(
                    throwableOf(msg)
                )

                washEffect.value = effectIdle()
                washProductionResult.value = ProductionResult.Fail(msg)
                toast.value = msg
            }
            val successBlock = {
                val msg = if (dest) TOAST_WASH_ON_SUCCESS else TOAST_WASH_OFF_SUCCESS
                washEffect.value = effectSuccess()
                washEffect.value = effectIdle()
                washProductionResult.value = ProductionResult.Success(msg)
                toast.value = msg
            }
            when (receiveData) {
                is ActionResult.Fail -> failBlock(if (dest) TOAST_WASH_ON_FAIL else TOAST_WASH_OFF_FAIL)
                is ActionResult.Success -> {
                    val receive = receiveData.data
                    if (dest == receive) {
                        successBlock()
                    } else {
                        failBlock(if (dest) TOAST_WASH_ON_FAIL else TOAST_WASH_OFF_FAIL)
                    }
                }

                ActionResult.TimeOut -> failBlock(if (dest) TOAST_WASH_ON_TIMEOUT else TOAST_WASH_OFF_TIMEOUT)
            }


            d("ProductionViewModel wash $dest end <=====")
        }.also {
//            _state.value = state.value.copy(
//                washEffect = Effect.Idle,
//            )
        }
    }

    fun power(dest: Boolean) {
        if (!connected.value) {
            toast(TOAST_BLUETOOTH_DISCONNECT)
            return
        }
        powerJob?.cancel()
        powerJob = viewModelScope.launch {
            val timeoutJob = launch {
                delay(TIMEOUT)
                d("ProductionViewModel power 超时")
                powerChannel?.trySend(ActionResult.TimeOut)
            }.also {
                it.invokeOnCompletion {
                    d("ProductionViewModel power 超时任务关闭")
                }
            }
            d("ProductionViewModel power $dest ===>")

            powerEffect.value = effectProgress()
            powerProductionResult.value = ProductionResult.Idle


            val channel = Channel<ActionResult<Boolean, String>>(1)
            powerChannel = channel
            d("ProductionViewModel channel ...")
            BleX.power(dest)
            val receiveData = channel.receive()
            d("ProductionViewModel receive power=$receiveData")
            channel.cancel()
            timeoutJob.cancel()
            powerChannel = null
            delay(DELAY)
            val successBlock = {
                val msg = if (dest) TOAST_POWER_ON_SUCCESS else TOAST_POWER_OFF_SUCCESS
                powerEffect.value = effectSuccess()
                powerEffect.value = effectIdle()
                powerProductionResult.value = ProductionResult.Success(msg)
                toast.value = msg

            }
            val failBlock = { msg: String ->
                powerEffect.value = effectFail(throwableOf { msg })

                powerEffect.value = Effect.Idle
                powerProductionResult.value = ProductionResult.Fail(
                    msg
                )
                toast.value = msg

            }
            when (receiveData) {
                is ActionResult.Fail -> failBlock(if (dest) TOAST_POWER_ON_FAIL else TOAST_POWER_OFF_FAIL)
                is ActionResult.Success -> {
                    val receive = receiveData.data
                    if (dest == receive) {
                        successBlock()
                    } else {
                        failBlock(if (dest) TOAST_POWER_ON_FAIL else TOAST_POWER_OFF_FAIL)
                    }
                }

                ActionResult.TimeOut -> failBlock(if (dest) TOAST_POWER_ON_TIMEOUT else TOAST_POWER_OFF_TIMEOUT)
            }


            d("ProductionViewModel power $dest end <=====")
        }.also {
//            _state.value = state.value.copy(
//                powerEffect = Effect.Idle,
//            )
        }
    }

    fun production() {
        if (!connected.value) {
            toast(TOAST_BLUETOOTH_DISCONNECT)
            return
        }
        productionJob?.cancel()
        productionJob = viewModelScope.launch {

            val timeoutJob = launch {
                delay(TIMEOUT)
                d("ProductionViewModel production 超时")
                productionChannel?.trySend(ActionResult.TimeOut)
            }.also {
                it.invokeOnCompletion {
                    d("ProductionViewModel production 超时任务关闭")
                }
            }
            d("ProductionViewModel power  ===>")

            productionEffect.value = effectProgress()
            productionProductionResult.value = ProductionResult.Idle

            val channel = Channel<ActionResult<BleEvent.Production, String>>(1)
            productionChannel = channel
            BleX.production()
            val receiveData = channel.receive()
            d("ProductionViewModel receive production=$receiveData")
            channel.cancel()
            timeoutJob.cancel()
            productionChannel = null
            delay(DELAY)

            when (receiveData) {
                is ActionResult.Fail -> {
                    val msg = TOAST_PRODUCTION_FAIL
                    productionEffect.value = effectFail(throwableOf { msg })
                    productionEffect.value = Effect.Idle
                    productionProductionResult.value = ProductionResult.Fail(msg)

                }

                is ActionResult.Success -> {
                    val receive = receiveData.data
                    if (receive.code.endsWith(sr.peiJianCode)) {
                        productionEffect.value = effectSuccess()
                        productionEffect.value = Effect.Idle
                        productionProductionResult.value = ProductionResult.Success(receive.display)
                        toast.value = TOAST_PRODUCTION_SUCCESS

                    } else {
                        val msg = "配件码不一致\n${receive.display}"
                        productionEffect.value = effectFail(throwableOf { msg })
                        productionEffect.value = Effect.Idle
                        productionProductionResult.value = ProductionResult.Fail(msg)

                    }

                }

                ActionResult.TimeOut -> {
                    val msg = TOAST_PRODUCTION_TIMEOUT
                    productionEffect.value = effectFail(throwableOf { msg })
                    productionEffect.value = Effect.Idle
                    productionProductionResult.value = ProductionResult.Fail(msg)

                }
            }
            d("ProductionViewModel production  end <=====")
        }.also {
//            _state.value = state.value.copy(
//                powerEffect = Effect.Idle,
//            )
        }
    }

    fun reset(ignore: Boolean = true) {
        if (!connected.value) {
            toast(TOAST_BLUETOOTH_DISCONNECT)
            return
        }
        commitEffect.value = effectProgress()
        resetJob?.cancel()
        resetJob = viewModelScope.launch {

            val timeoutJob = launch {
                delay(TIMEOUT)
                d("ProductionViewModel reset 超时")
                resetChannel?.trySend(ActionResult.TimeOut)
            }.also {
                it.invokeOnCompletion {
                    d("ProductionViewModel reset 超时任务关闭")
                }
            }
            d("ProductionViewModel reset  ===>")

//            resetEffect.value = effectProgress()
//            resetResult.value = ProductionResult.Idle

            val channel = Channel<ActionResult<Boolean, String>>(1)
            resetChannel = channel
            BleX.reset()
            val receiveData = channel.receive()
            d("ProductionViewModel receive reset=$receiveData")
            channel.cancel()
            timeoutJob.cancel()
            resetChannel = null
            delay(DELAY)

            // if (ignore) return@launch
            when (receiveData) {
                is ActionResult.Fail -> {
                    val msg = "退出产测模式失败"
                    commitEffect.value = effectFail(throwableOf { msg })
                    toast.value = msg
//                    resetEffect.value = effectFail(throwableOf { msg })
//                    resetEffect.value = Effect.Idle
//                    resetResult.value = ProductionResult.Fail(msg)

                }

                is ActionResult.Success -> {
                    val receive = receiveData.data
                    if (receive
                    ) {
//                        resetEffect.value = effectSuccess()
//                        resetEffect.value = Effect.Idle
//                        resetResult.value = ProductionResult.Success("")
                        toast.value = ""

                        commit()

                    } else {
                        val msg = "退出产测模式失败"
//                        resetEffect.value = effectFail(throwableOf { msg })
//                        resetEffect.value = Effect.Idle
//                        resetResult.value = ProductionResult.Fail(msg)
                        commitEffect.value = effectFail(throwableOf { msg })
                        toast.value = msg
                    }
                }

                ActionResult.TimeOut -> {
                    val msg = "退出产测模式超时"
//                    resetEffect.value = effectFail(throwableOf { msg })
//                    resetEffect.value = Effect.Idle
//                    resetResult.value = ProductionResult.Fail(msg)
                    commitEffect.value = effectFail(throwableOf { msg })
                    toast.value = msg
                }
            }
            d("ProductionViewModel reset  end <=====")
        }.also {
//            _state.value = state.value.copy(
//                powerEffect = Effect.Idle,
//            )
        }
    }

    private suspend fun commit(): Boolean {
        return withContext(Dispatchers.IO) {
            //commitEffect.value = effectProgress()
            val token = user.value.token
            val product = pt
            check(product != null) {
                "product is null"
            }
            val productModel = pt.productModel
            check(productModel != null) {
                "productModel is null"
            }
            val scanResult = sr
            check(scanResult != null) {
                "scanResult is null"
            }
//            val map = mapOf(
//                "isOpen" to when (powerProductionResult.value) {
//                    is ProductionResult.Fail -> 0
//                    ProductionResult.Idle -> -1
//                    is ProductionResult.Success -> 1
//                }, "wash" to when (washProductionResult.value) {
//                    is ProductionResult.Fail -> 0
//                    ProductionResult.Idle -> -1
//                    is ProductionResult.Success -> 1
//                }, "chanCe" to when (val s = productionProductionResult.value) {
//                    is ProductionResult.Fail -> 0
//                    ProductionResult.Idle -> -1
//                    is ProductionResult.Success -> 1
//                }
//            )

            val productResult = ProductResult(
//                user = user.value,
                connected = connected.value,
                productResult = when (productionProductionResult.value) {
                    is ProductionResult.Fail -> 0
                    ProductionResult.Idle -> -1
                    is ProductionResult.Success -> 1
                },
                washResult = when (washProductionResult.value) {
                    is ProductionResult.Fail -> 0
                    ProductionResult.Idle -> -1
                    is ProductionResult.Success -> 1
                },
                openResult = when (powerProductionResult.value) {
                    is ProductionResult.Fail -> 0
                    ProductionResult.Idle -> -1
                    is ProductionResult.Success -> 1
                },
//                bleLog = if (debug.value) bleLogs.value.map {
//                    datetimeFormat { it.timestamp.toStr() + " " + it.log }
//                } else emptyList(),

                bleLog = if (true || debug.value) bleSimpleLogs.value else emptyList(),
                rawDeviceInfo = (properties.value.map { it }.joinToString() { it.toString() }),
//                device = scanResult
            )

            val json = myJson.encodeToString(productResult)
            println("json = $json")
            return@withContext try {
                val req = if (product.type == ProductType.PeiJian) {
                    FinishReq.PeiJian(
                        componentCode = scanResult.peiJianCode,
                        result = json
                    )
                } else {
                    FinishReq.Device(
                        code = scanResult.wuLiuCode,
                        componentCode = scanResult.peiJianCode,
                        productCode = productModel.identifier,
                        result = json
                    )
                }
                val result = appRepo.finish(
                    token = token, req
                )
                delay(1200)
                when (result) {
                    is HDResult.Fail -> {
                        commitEffect.value = effectFail(result.error)
                        toast.value = result.error.message ?: "提交失败"
                        false
                    }

                    is HDResult.Success -> {
                        commitEffect.value = effectSuccess()
                        true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = e.message ?: "提交失败"
                commitEffect.value = effectFail(e)
                toast.value = msg
                false
            }
        }

    }

    private fun d(msg: String) {
        Napier.d(tag = "vm") {
            msg
        }
    }

    private fun w(msg: String) {
        Napier.w(tag = "vm") {
            msg
        }
    }

    fun connect() {
        autoConnect()
    }

    fun debug(on: Boolean) {
        debug.value = on
    }

    companion object {
        private const val DELAY = 1000L
        private const val TIMEOUT = 5000L
        private const val RETRY_MAX_SCAN = 2
        private const val RETRY_MAX_PRODUCTION = 1
        private const val TOAST_POWER_ON_SUCCESS = "开机成功"
        private const val TOAST_POWER_OFF_SUCCESS = "关机成功"
        private const val TOAST_POWER_ON_FAIL = "设备开机失败，请稍后再试"
        private const val TOAST_POWER_ON_TIMEOUT = "设备开机超时，请稍后再试"
        private const val TOAST_POWER_OFF_FAIL = "设备关机失败，请稍后再试"
        private const val TOAST_POWER_OFF_TIMEOUT = "设备关机超时，请稍后再试"

        private const val TOAST_WASH_ON_SUCCESS = "冲洗成功"
        private const val TOAST_WASH_OFF_SUCCESS = "关闭冲洗成功"
        private const val TOAST_WASH_ON_FAIL = "冲洗失败，请稍后再试"
        private const val TOAST_WASH_ON_TIMEOUT = "冲洗超时，请稍后再试"
        private const val TOAST_WASH_OFF_FAIL = "关闭冲洗失败，请稍后再试"
        private const val TOAST_WASH_OFF_TIMEOUT = "关闭冲洗超时，请稍后再试"

        private const val TOAST_PRODUCTION_SUCCESS = "获取数据成功"
        private const val TOAST_PRODUCTION_FAIL = "获取数据失败，请稍后再试"
        private const val TOAST_PRODUCTION_TIMEOUT = "获取数据超时，请稍后再试"
        internal const val TOAST_BLUETOOTH = "蓝牙未开启，请打开手机蓝牙"
        internal const val TOAST_BLUETOOTH_DISCONNECT = "设备未连接"
    }
}