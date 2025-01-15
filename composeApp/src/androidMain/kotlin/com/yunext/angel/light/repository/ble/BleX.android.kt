package com.yunext.angel.light.repository.ble

import android.os.Build
import com.juul.kable.AndroidPeripheral
import com.juul.kable.Peripheral
import io.github.aakira.napier.Napier

actual suspend fun Peripheral.requestMtuIfNeed(mtu: Int): Boolean {
    Napier.d { "requestMtuIfNeed dest:$mtu" }
    return try {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            false
        } else {
            val p = (this as AndroidPeripheral)
            val requestMtu = p.requestMtu(mtu)
            Napier.d { "requestMtuIfNeed result:$requestMtu" }
            return true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}