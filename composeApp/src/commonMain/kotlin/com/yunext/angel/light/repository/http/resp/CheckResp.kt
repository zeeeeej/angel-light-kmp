package com.yunext.angel.light.repository.http.resp

import kotlinx.serialization.Serializable

@Serializable
data class CheckResp(
    val code: String?,
    val componentCode: String?,
    val productCode: String?,
    val productName: String?,
    val productImg: String?,
    val seriesName: String?
)