package com.yunext.angel.light.repository.ble

import com.juul.kable.AndroidPeripheral
import com.juul.kable.Peripheral
import kotlinx.coroutines.flow.first

actual suspend fun Peripheral.requestMtuIfNeed(mtu: Int) {
    val p = (this as AndroidPeripheral)
    p.requestMtu(mtu)
    p.mtu.first{
        it == mtu
    }
}