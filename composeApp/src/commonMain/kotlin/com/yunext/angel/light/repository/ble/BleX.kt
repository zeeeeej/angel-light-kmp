package com.yunext.angel.light.repository.ble

import BleEvent
import com.juul.kable.ExperimentalApi
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Hex
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

expect suspend fun Peripheral.requestMtuIfNeed(mtu: Int)

object BleX {

    private const val TAG = "blex"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var autoConnectJob: Job? = null
    private val upChannel: Channel<ByteArray> = Channel()
    val downChannel: Channel<BleEvent> = Channel()

    init {

        authResp()
    }


    fun wash(on: Boolean) {
        val value = if (on) byteArrayOf(1) else byteArrayOf(0)
        val no = byteArrayOf(0x03)
        val length = byteArrayOf((value + no).size.toByte())
        val payload = length + no + value
        val power = Protocol.rtcData(
            ProtocolCmd.DeviceInfoUpdateWrite, payload
        )
        upChannel.trySend(power.toByteArray())
    }

    fun power(on: Boolean) {
        val value = if (on) byteArrayOf(1) else byteArrayOf(0)
        val no = byteArrayOf(0x02)
        val length = byteArrayOf((value + no).size.toByte())
        val payload = length + no + value
        val power = Protocol.rtcData(
            ProtocolCmd.DeviceInfoUpdateWrite, payload
        )
        upChannel.trySend(power.toByteArray())
    }

    fun reset() {
        val value = byteArrayOf(0)
        val no = byteArrayOf(0x21)
        val length = byteArrayOf((value + no).size.toByte())
        val payload = length + no + value
        val power = Protocol.rtcData(
            ProtocolCmd.DeviceInfoUpdateWrite, payload
        )
        upChannel.trySend(power.toByteArray())
    }


    private fun authResp() {
        val payload = byteArrayOf(0x00)
        val authResp = Protocol.rtcData(
            ProtocolCmd.AuthNotify, payload
        )
        @OptIn(ExperimentalStdlibApi::class)
        d("authResp:${authResp.toByteArray().toHexString()}")
    }

    fun production() {
        val payload = byteArrayOf()
        val power = Protocol.rtcData(
            ProtocolCmd.ProductionWriteNotify, payload
        )
        upChannel.trySend(power.toByteArray())
    }

    private val upSyncDeviceInfo: () -> ByteArray? = {
        try {

            val syncDeviceInfo = Protocol.rtcData(
                ProtocolCmd.DeviceInfoSelectWrite,
                byteArrayOf()
            )
            val payload = syncDeviceInfo.toByteArray()
            (payload)
        } catch (e: Exception) {
            e.printStackTrace()
            //trySend(BleEvent.Error("auth ${e.message}"))
            null
        }
    }

    private val upAuthBlock: (String) -> ByteArray? = { name: String ->

        try {
            val dest = Protocol.parseFromBroadcast(name)
            check(!dest.isNullOrEmpty()) {
                "dest为空"
            }
            val authenticationWrite = Protocol.authenticationWrite(
                Protocol.ACCESSKEY,
                dest
            )
            val authWrite = authenticationWrite.toByteArray()
            d("开始鉴权[${ProtocolCmd.AuthWrite}] 目标：${dest}")
            authWrite
        } catch (e: Exception) {
            e.printStackTrace()
            d("鉴权失败$e")
            null
            //trySend(BleEvent.Error("auth ${e.localizedMessage}"))
        }
    }
    private var _peripheral: Peripheral? = null

    @OptIn(ExperimentalStdlibApi::class, ExperimentalApi::class)
    fun start() {
        autoConnectJob?.cancel()
        autoConnectJob = scope.launch {
            launch {
                val serviceUUID = Protocol.UUID_SERVICE
                val notifyUUID = Protocol.UUID_CH_NOTIFY
                val writeUUID = Protocol.UUID_CH_WRITE
                d { "*>>>>>>>>>>>>>>>>>>>>" }
                d { "serviceUUID    :   $serviceUUID" }
                d { "notifyUUID     :   $notifyUUID" }
                d { "writeUUID      :   $writeUUID" }
                val scanner = Scanner {
                    filters {
                        match {
                            name = Filter.Name.Prefix(Protocol.PREFIX)
                        }
                    }
                    logging {
                        engine = SystemLogEngine
                        level = Logging.Level.Warnings
                        format = Logging.Format.Multiline
                    }
                }
                val todo = scanner.advertisements.first()
                d { "<<<搜索到外设: $todo" }
                val peripheral = Peripheral(todo) {
                    logging {
                        engine = SystemLogEngine
                        level = Logging.Level.Warnings
                        format = Logging.Format.Multiline
                        data = Hex
                        identifier = TAG
                    }

                    onServicesDiscovered {
                        d { "<<<onServicesDiscovered" }
                    }

                    observationExceptionHandler { cause ->
                        // Only propagate failure if we don't see a disconnect within a second.
                        withTimeoutOrNull(1_000L) { state.first { it is State.Disconnected } }
                            ?: throw IllegalStateException(
                                "Observation failure occurred.",
                                cause
                            )
                        e("Ignored failure associated with disconnect: $cause")
                    }
                }

                launch {
                    peripheral.services.collect {
                        d("<<<services ${
                            it?.joinToString {
                                "\n ${it.serviceUuid}"
                            }
                        }")

                        try {
                            it?.filter {
                                it.serviceUuid.toString() == serviceUUID
                            }?.first()?.characteristics?.forEach {
                                d(
                                    """
                                    ${it.characteristicUuid} ${it.properties.toString()}
                                """.trimIndent()
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                //<editor-fold desc="write">
                val writeCH = characteristicOf(serviceUUID, writeUUID)
                val writeBlock: suspend (ByteArray) -> Unit =
                    { up: ByteArray ->
                        d("写入${up.display}")
//                        scope.launch {
                        peripheral.write(writeCH, up)
//                        }
                    }

                launch {
                    upChannel.receiveAsFlow().collect {
                        writeBlock(it)
                    }
                }
                //</editor-fold>

                //<editor-fold desc="notify">
                val notify = {
                    launch {
                        peripheral
                            .observe(
                                characteristic = characteristicOf(serviceUUID, notifyUUID),
                                onSubscription = {
                                    d(">>>onSubscription")
                                }
                            )
                            .collect { down ->
                                d("<<<down $serviceUUID/$notifyUUID ${down.toHexString()}")
                                try {
                                    val bleEvent: BleEvent = parseDataV2(down = down)
                                    when (bleEvent) {
                                        is BleEvent.Authed -> {
                                            val syncDeviceInfo = upSyncDeviceInfo()
                                            if (syncDeviceInfo != null) {
                                                d(">>>开始同步设备信息")
                                                writeBlock(syncDeviceInfo)
                                            } else {
                                                w(">>>开始同步设备信息为空")
                                            }
                                        }

                                        else -> {

                                        }
                                    }
                                    downChannel.send(bleEvent)
                                } catch (e: Exception) {
                                    e("<<<解析异常:$e")
                                }
                            }
                    }

                }
                //</editor-fold>

                //<editor-fold desc="stateChanged">
                launch {
                    peripheral.state.collect {
                        d("<<<当前状态：$it")
                        when (it) {
                            State.Connected -> {
                                d {
                                    ">>>打开Notify..."
                                }
                                notify()
                                d {
                                    ">>>requestMtuIfNeed..."
                                }
                                peripheral.requestMtuIfNeed(240)
                                d {
                                    ">>>requestMtuIfNeed!"
                                }
                                delay(1000)
                                val authData = upAuthBlock(peripheral.name ?: "")
                                if (authData != null) {
                                    d {
                                        ">>>开始鉴权..."
                                    }
                                    writeBlock(authData)
                                }
                            }

                            State.Connecting.Bluetooth -> {

                            }

                            State.Connecting.Observes -> {

                            }

                            State.Connecting.Services -> {

                            }

                            is State.Disconnected -> {
                                downChannel.trySend(
                                    BleEvent.Disconnect(
                                        isActiveDisConnected = true,
                                        msg = it.status?.toString() ?: ""
                                    )
                                )
                            }

                            State.Disconnecting -> {

                            }
                        }
                    }
                }
                //</editor-fold>

                d(">>>开始连接")
                _peripheral = peripheral

                try {
                    peripheral.connect()
                } catch (e: Exception) {
                    e("<<<peripheral.connect() error")
                    e.printStackTrace()
                    if (e is CancellationException) {
                        throw e
                    }
                }
            }.also {
                it.invokeOnCompletion {
                    d { "<<<<<<<<<<<<<<<<<*" }
                    scope.launch(Dispatchers.Main + NonCancellable) {
                        try {
                            _peripheral?.disconnect()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }
            }
        }
    }

    private suspend fun parseDataV2(down: ByteArray): BleEvent {

        // 5aa5b2001000b2
        val parseDataInternal: suspend (ByteArray) -> ProtocolData = { data: ByteArray ->
            require(data.size >= 6) {
                "数据错误$data"
            }
            require(byteArrayOf(data[0], data[1]).contentEquals(Protocol.HEAD)) {
                "数据头错误$data"
            }
            val length = byteArrayOf(data[3], data[4])

            @OptIn(ExperimentalStdlibApi::class)
            val lengthInt = byteArrayOf(data[3], data[4]).toHexString().toInt(16)

            if (lengthInt == 0) {
                require(data.size == 6) {
                    "数据长度错误2$data"
                }
            } else {
                require(data.size - 6 == lengthInt) {
                    "数据长度错误1$data"
                }
            }

            val crc = (data.dropLast(1).sum() % 256).toByte()
            require(crc == data[data.size - 1]) {
                "crc错误$data"
            }
            val cmd = data[2]
            val payload =
                if (lengthInt == 0) byteArrayOf() else data.copyOfRange(5, 5 + lengthInt)
            ProtocolData(
                head = Protocol.HEAD, cmd = cmd, length = length, payload = payload, crc = crc
            )
        }

        // 解析
        val protocolData = parseDataInternal(down)
        val result = when (protocolData.cmd) {
            ProtocolCmd.AuthNotify.cmd -> {
                val payload = protocolData.payload
                val success = payload.contentEquals(byteArrayOf(0x00))
                BleEvent.Authed(success)
            }

            ProtocolCmd.VersionNotify.cmd -> {
                val payload = protocolData.payload
                val version = payload.decodeToString()
                BleEvent.Version(version)
            }

            ProtocolCmd.DeviceInfoSelectNotify.cmd -> {
                // 5aa5a2008a19013131373338373632303937363535373932383131000000000502006400640503006400000504000000000505000000000306000005070000000002080105090000000105100000000005130000000005140000000005150000001905160000000408175357312e302e35051800003bd205190000000005200000453105210000095f052200000000cc
                val payload = protocolData.payload
                val map = parseDeviceInfo(payload)
                BleEvent.DeviceInfo(map)
            }

            ProtocolCmd.ProductionWriteNotify.cmd -> {
                val payload = protocolData.payload
                parseProduction(payload)
            }

            ProtocolCmd.DeviceInfoUpdateNotify.cmd -> {
                val payload = protocolData.payload

                val r = parseSetDeviceInfo(payload)
                BleEvent.SetDeviceResult(r)

            }

            else -> {
                BleEvent.Notify(data = parseDataInternal(down))
            }
        }
        return result

    }

    fun stop() {
        autoConnectJob?.cancel()
        autoConnectJob = null
    }


    private fun d(msg: String) {
        Napier.d(tag = TAG) {
            msg

        }
    }

    private fun d(msgBlock: () -> String) {
        Napier.d(tag = TAG) {
            msgBlock()
        }
    }

    private fun e(msg: String) {
        Napier.e(tag = TAG) {
            msg

        }
    }

    private fun e(msgBlock: () -> String) {
        Napier.e(tag = TAG) {
            msgBlock()
        }
    }

    private fun ProducerScope<BleEvent>.msg(msg: String, extra: (String) -> Unit) {
        extra(msg)
        trySend(BleEvent.Msg(msg = msg))
    }

    private fun msg2(msg: String, extra: (String) -> Unit) {
        extra(msg)
        downChannel.trySend(BleEvent.Msg(msg = msg))
    }
}

val TEST_Service = "00000000-1001-1000-6864-79756e657874"

val TEST_CH = "0000a201-1001-1000-6864-79756e657874"
