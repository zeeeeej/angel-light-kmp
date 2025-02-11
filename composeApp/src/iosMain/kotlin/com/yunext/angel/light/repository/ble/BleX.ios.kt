package com.yunext.angel.light.repository.ble

import com.juul.kable.Peripheral
import com.juul.kable.PlatformScanner
import com.juul.kable.Scanner

actual suspend fun Peripheral.requestMtuIfNeed(mtu: Int):Boolean {
    return true
}

actual fun createPlatformScanner():PlatformScanner{
    return Scanner{

    }
}