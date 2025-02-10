package com.yunext.angel.light.domain.poly

import com.yunext.angel.light.repository.ble.TslPropertyKey
import kotlinx.serialization.Serializable

@Serializable
data class ProductResult(
//    val user: User,
    val connected: Boolean,
    val rawDeviceInfo: List<PropertyInfo>,
    val openResult: Int,
    val washResult: Int,
    val productResult: Int,
    val bleLog: List<String>,
//    val device:ScanResult
    val phone:String
)

@Serializable
data class PropertyInfo(
    val key: TslPropertyKey,
    val value: String,
    val unit: String,
    val name: String,
    val rawValue: String
) {
    override fun toString(): String {
        return "[$name]$key=$value<0x$rawValue>${if (unit.isNotEmpty()) "($unit)" else ""}"
    }
}