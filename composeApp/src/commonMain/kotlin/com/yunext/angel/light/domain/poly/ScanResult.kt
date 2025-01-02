package com.yunext.angel.light.domain.poly

import kotlinx.serialization.Serializable

@Serializable
data class ScanResult(
    val peiJianCode: String, val wuLiuCode: String, val productCode: String,
    val productName: String,
    val identifier: String,
    val img: String,
    val modelName:String
)