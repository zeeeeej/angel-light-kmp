package com.yunext.angel.light.domain.poly

import kotlinx.serialization.Serializable

@Serializable
data class ProductResult(
//    val user: User,
    val connected:Boolean,
    val rawDeviceInfo: String,
    val openResult: Int,
    val washResult: Int,
    val productResult: Int,
    val bleLog: List<String>,
//    val device:ScanResult
)