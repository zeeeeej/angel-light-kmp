package com.yunext.angel.light.ui.vo

import kotlinx.datetime.Clock

data class BleLog(
    val log: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val index: Long = INDEX++
) {
    companion object {
        private var INDEX = 0L
    }
}