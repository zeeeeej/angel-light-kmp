package com.yunext.angel.light.ui.vo

import com.yunext.angel.light.repository.ble.TslPropertyKey
import kotlinx.serialization.Serializable

@Serializable
data class PropertyVo(
    val key: TslPropertyKey,
    val value: String,
    val unit: String,
    val name: String,
) {
    override fun toString(): String {
        return "[$name]$key=$value${if (unit.isNotEmpty()) "($unit)" else ""}"
    }
}