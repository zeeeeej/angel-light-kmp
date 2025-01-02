package com.yunext.angel.light.repository.ble

import io.github.aakira.napier.Napier


val ByteArray.display: String
    @OptIn(ExperimentalStdlibApi::class)
    get() = "[${this.toHexString()}]"

private const val TAG = "_ble"

fun d(msg: String) {
    Napier.d(tag = TAG){msg}
}

fun e(msg: String) {
    Napier.e(tag = TAG){msg}
}

fun w(msg: String) {
    Napier.w(tag = TAG){msg}
}

fun i(msg: String) {
    Napier.i(tag = TAG){msg}
}


